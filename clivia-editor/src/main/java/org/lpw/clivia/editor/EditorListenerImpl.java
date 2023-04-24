package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service(EditorModel.NAME + ".listener")
public class EditorListenerImpl implements EditorListener {
    @Override
    public String name() {
        return EditorModel.NAME;
    }

    @Override
    public String title(String key) {
        return "editor";
    }

    @Override
    public JSONArray get(String key) {
        JSONObject object = new JSONObject();
        object.put("tag", "text");
        JSONObject text = new JSONObject();
        text.put("text", "");
        JSONArray texts = new JSONArray();
        texts.add(text);
        object.put("texts", texts);
        JSONArray array = new JSONArray();
        array.add(object);

        return array;
    }

    @Override
    public void put(String key, JSONArray array) {
    }
}
