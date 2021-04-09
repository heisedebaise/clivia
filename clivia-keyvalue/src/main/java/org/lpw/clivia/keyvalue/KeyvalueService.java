package org.lpw.clivia.keyvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface KeyvalueService {
    String VALIDATOR_KEY_NOT_EXISTS = KeyvalueModel.NAME + ".validator.key.not-exists";

    JSONObject query(String key);

    JSONArray list(String key);

    JSONObject object(String key);

    boolean exists(String key);

    String value(String key);

    int valueAsInt(String key, int defaultValue);

    double valueAsDouble(String key, double defaultValue);

    void save(String key, String value);

    void save(KeyvalueModel keyvalue);

    void saves(JSONArray array);

    void delete(String id);
}
