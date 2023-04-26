package org.lpw.clivia.openai;

import com.alibaba.fastjson.JSONObject;

public interface OpenaiService {
    String KEY_NOT_EXISTS_VALIDATOR = OpenaiModel.NAME + ".validator.key.not-exists";

    JSONObject query(String key);

    void save(OpenaiModel openai);

    void delete(String id);

    String chat(String key, String name, String content);
}
