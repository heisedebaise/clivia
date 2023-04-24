package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;

public interface EditorListener {
    String name();

    String title(String key);

    JSONArray get(String key);

    void put(String key, JSONArray array);
}
