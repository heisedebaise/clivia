package org.lpw.clivia.category;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

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
    public JSONObject find(String id) {
        return cache.computeIfAbsent(CategoryModel.NAME + id, key -> {
            CategoryModel category = categoryDao.findById(id);

            return category == null ? new JSONObject() : modelHelper.toJson(category);
        }, false);
    }

    @Override
    public void save(CategoryModel category) {
        if (validator.isEmpty(category.getId()) || categoryDao.findById(category.getId()) == null)
            category.setId(null);
        if (validator.isEmpty(category.getParent()) || categoryDao.findById(category.getParent()) == null)
            category.setParent("");
        categoryDao.save(category);
        cache.remove(CategoryModel.NAME + category.getKey());
        cache.remove(CategoryModel.NAME + category.getId());
    }

    @Override
    public void delete(String id) {
        CategoryModel category = categoryDao.findById(id);
        if (category == null)
            return;

        categoryDao.delete(id);
        cache.remove(CategoryModel.NAME + category.getKey());
        cache.remove(CategoryModel.NAME + category.getId());
    }
}
