package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;

public interface EditorListener {
    String name();

    JSONArray get(String key);

    void put(String key, JSONArray array);
}
