package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface EditorService {
    JSONArray get(String listener, String key);

    JSONArray empty();

    JSONObject put(String listener, String key, String id, JSONArray lines, long sync);

    JSONObject view(String listener, String key);

    boolean ai();

    String aiText(String content);

    String aiImage(String content);
}
