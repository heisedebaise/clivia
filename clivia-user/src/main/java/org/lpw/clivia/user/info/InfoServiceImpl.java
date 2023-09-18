package org.lpw.clivia.user.info;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

@Service(InfoModel.NAME + ".service")
public class InfoServiceImpl implements InfoService, ContextRefreshedListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private InfoDao infoDao;

    @Override
    public JSONObject user() {
        return get(userService.id());
    }

    @Override
    public JSONObject get(String user) {
        return cache.computeIfAbsent(InfoModel.NAME + ":" + user, key -> {
            JSONObject object = new JSONObject();
            infoDao.query(user).getList().forEach(info -> object.put(info.getName(), info.getValue()));

            return object;
        }, false);
    }

    @Override
    public JSONObject get(String user, String name) {
        InfoModel info = infoDao.find(validator.isId(user) ? user : userService.id(), name);

        return info == null ? new JSONObject() : modelHelper.toJson(info);
    }

    @Override
    public Set<String> users(Map<String, String> map) {
        Set<String> set = null;
        for (String name : map.keySet()) {
            String value = map.get(name);
            if (validator.isEmpty(value))
                continue;

            Set<String> s = infoDao.user(name, value);
            if (s.isEmpty())
                return s;

            if (set == null)
                set = s;
            else {
                set.retainAll(s);
                if (set.isEmpty())
                    return set;
            }
        }

        return set;
    }

    @Override
    public void save(String name, String value) {
        String user = userService.id();
        save(infoDao.find(user, name), user, name, value, true);
    }

    @Override
    public void save(String user, String name, String value) {
        if (validator.isEmpty(value))
            return;

        InfoModel info = infoDao.find(user, name);
        if (info == null || validator.isEmpty(info.getValue()))
            save(info, user, name, value, true);
    }

    @Override
    public void save(Map<String, String> map) {
        String user = userService.id();
        map.forEach((name, value) -> save(infoDao.find(user, name), user, name, value, false));
        clearCache(user);
    }

    private void save(InfoModel info, String user, String name, String value, boolean clear) {
        if (info == null) {
            info = new InfoModel();
            info.setUser(user);
            info.setName(name);
        }
        info.setValue(value);
        info.setTime(dateTime.now());
        infoDao.save(info);
        if (clear)
            clearCache(user);
    }

    @Override
    public void delete(String user) {
        infoDao.delete(user);
        clearCache(user);
    }

    private void clearCache(String user) {
        cache.remove(InfoModel.NAME + ":" + user);
        userService.clearCache(user);
    }

    @Override
    public int getContextRefreshedSort() {
        return 151;
    }

    @Override
    public void onContextRefreshed() {
        if (infoDao.count() > 0)
            return;

        userService.query().forEach(user -> {
            save(user, "idcard", user.getIdcard());
            save(user, "name", user.getName());
            save(user, "mobile", user.getMobile());
            save(user, "email", user.getEmail());
            save(user, "weixin", user.getWeixin());
            save(user, "qq", user.getQq());
            save(user, "nick", user.getNick());
            save(user, "avatar", user.getAvatar());
            save(user, "signature", user.getSignature());
            if (user.getGender() > 0)
                save(user, "gender", numeric.toString(user.getGender()));
            if (user.getBirthday() != null)
                save(user, "birthday", dateTime.toString(user.getBirthday()));
            save(user, "from", user.getFrom());
        });
        infoDao.close();
    }

    private void save(UserModel user, String name, String value) {
        if (validator.isEmpty(value))
            return;

        InfoModel info = new InfoModel();
        info.setUser(user.getId());
        info.setName(name);
        info.setValue(value);
        info.setTime(dateTime.now());
        infoDao.save(info);
    }
}
