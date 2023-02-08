package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface EditorService {
    void put(String key, JSONArray array);

    JSONArray get(String key, boolean empty);

    JSONArray query(String key);

    JSONObject save(String key, JSONObject content);

    void order(String id, int order);

    void delete(String id);
}
