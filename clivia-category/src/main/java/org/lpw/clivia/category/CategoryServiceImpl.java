package org.lpw.clivia.category;

import com.alibaba.fastjson.JSONArray;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Service(CategoryModel.NAME + ".service")
public class CategoryServiceImpl implements CategoryService {
    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Optional<Set<CategoryListener>> listeners;
    @Inject
    private CategoryDao categoryDao;

    @Override
    public JSONArray query(String key) {
        return cache.computeIfAbsent(CategoryModel.NAME + key, k -> query(key, ""), false);
    }

    private JSONArray query(String key, String parent) {
        return modelHelper.toJson(categoryDao.query(key, parent).getList(), (category, object) -> {
            JSONArray children = query(key, category.getId());
            if (!children.isEmpty())
                object.put("children", children);
        });
    }

    @Override
    public String name(String id) {
        return cache.computeIfAbsent(CategoryModel.NAME + ":name:" + id, key -> {
            String name = "";
            String pid = id;
            while (!validator.isEmpty(pid)) {
                CategoryModel category = categoryDao.findById(pid);
                if (category == null)
                    break;

                name = "/" + category.getName() + name;
                pid = category.getParent();
            }

            return validator.isEmpty(name) ? "" : name.substring(1);
        }, false);
    }

    @Override
    public void save(CategoryModel category) {
        if (validator.isEmpty(category.getId()) || categoryDao.findById(category.getId()) == null)
            category.setId(null);
        if (validator.isEmpty(category.getParent()) || categoryDao.findById(category.getParent()) == null)
            category.setParent("");
        categoryDao.save(category);
        clean(category.getKey(), category.getId());

        listeners.ifPresent(set -> {
            Set<String> parents = new HashSet<>();
            for (String parent = category.getParent(); !validator.isEmpty(parent); ) {
                parents.add(parent);
                CategoryModel model = categoryDao.findById(parent);
                if (model != null)
                    parent = model.getParent();
            }
            Set<String> children = new HashSet<>();
            children(children, category.getKey(), category.getId());
            set.forEach(listener -> listener.categoryChanged(category.getId(), parents, children));
        });
    }

    private void children(Set<String> set, String key, String id) {
        categoryDao.query(key, id).getList().forEach(category -> {
            set.add(category.getId());
            children(set, key, category.getId());
        });
    }

    @Override
    public void delete(String id) {
        CategoryModel category = categoryDao.findById(id);
        if (category == null)
            return;

        categoryDao.delete(id);
        clean(category.getKey(), id);
    }

    private void clean(String key, String id) {
        cache.remove(CategoryModel.NAME + key);
        cache.remove(CategoryModel.NAME + ":name:" + id);
    }
}
