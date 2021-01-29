package org.lpw.clivia.keyvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(KeyvalueModel.NAME + ".service")
public class KeyvalueServiceImpl implements KeyvalueService {
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Cache cache;
    @Inject
    private Generator generator;
    @Inject
    private Numeric numeric;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueDao keyvalueDao;

    @Override
    public JSONObject query(String key) {
        return validator.isEmpty(key) ? keyvalueDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson()
                : keyvalueDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray list(String key) {
        return modelHelper.toJson(keyvalueDao.query(key, 0, 0).getList());
    }

    @Override
    public JSONObject object(String key) {
        return cache.computeIfAbsent(getCacheKey(":object:" + key), k -> {
            JSONObject object = new JSONObject();
            keyvalueDao.query(key, 0, 0).getList().forEach(keyvalue -> object.put(keyvalue.getKey(), keyvalue.getValue()));

            return object;
        }, false);
    }

    @Override
    public String value(String key) {
        return cache.computeIfAbsent(getCacheKey(":value:" + key), k -> {
            KeyvalueModel keyvalue = keyvalueDao.findByKey(key);

            return keyvalue == null ? "" : keyvalue.getValue();
        }, false);
    }

    @Override
    public int valueAsInt(String key, int defaultValue) {
        return numeric.toInt(value(key), defaultValue);
    }

    @Override
    public double valueAsDouble(String key, double defaultValue) {
        return numeric.toDouble(value(key), defaultValue);
    }

    @Override
    public void save(String key, String value) {
        KeyvalueModel keyvalue = keyvalueDao.findByKey(key);
        if (keyvalue == null) {
            keyvalue = new KeyvalueModel();
            keyvalue.setKey(key);
        }
        keyvalue.setValue(value);
        keyvalueDao.save(keyvalue);
    }

    @Override
    public void save(KeyvalueModel keyvalue) {
        if (validator.isEmpty(keyvalue.getId()) || keyvalueDao.findById(keyvalue.getId()) == null)
            keyvalue.setId(null);
        keyvalueDao.save(keyvalue);
        cleanCache();
    }

    @Override
    public void saves(JSONArray array) {
        if (validator.isEmpty(array))
            return;

        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            if (!object.containsKey("key") || !object.containsKey("value"))
                continue;

            String key = object.getString("key");
            if (validator.isEmpty(key))
                continue;

            KeyvalueModel keyvalue = keyvalueDao.findByKey(key);
            if (keyvalue == null) {
                keyvalue = new KeyvalueModel();
                keyvalue.setKey(key);
            }
            keyvalue.setValue(object.getString("value"));
            keyvalueDao.save(keyvalue);
        }
        cleanCache();
    }

    @Override
    public void delete(String id) {
        keyvalueDao.delete(id);
    }

    private String getCacheKey(String key) {
        return KeyvalueModel.NAME + key + cache.computeIfAbsent(KeyvalueModel.NAME, k -> generator.random(32), false);
    }

    private void cleanCache() {
        cache.remove(KeyvalueModel.NAME);
    }
}
