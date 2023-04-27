package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(EditorModel.NAME + ".listener")
public class EditorListenerImpl implements EditorListener {
    @Inject
    private EditorService editorService;

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
        return editorService.empty();
    }

    @Override
    public void put(String key, JSONArray array) {
    }
}
