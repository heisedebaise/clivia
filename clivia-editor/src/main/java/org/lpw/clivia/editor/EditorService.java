package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;

public interface EditorService {
    void put(String key, JSONArray array);

    JSONArray get(String key);
}
