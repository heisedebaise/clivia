package org.lpw.clivia.dict;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface DictService {
    String VALIDATOR_VALUE_NOT_EXISTS = DictModel.NAME + ".validator.value.not-exists";

    JSONObject query(String key);

    JSONArray list(String key);

    String name(String key, String value);

    void save(DictModel dict);

    void delete(String id);
}
