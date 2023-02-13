package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(EditorModel.NAME + ".service")
public class EditorServiceImpl implements EditorService {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    private final Map<String, JSONObject> lines = new ConcurrentHashMap<>();
    private final Map<String, List<String>> ids = new ConcurrentHashMap<>();

    @Override
    public void put(String key, JSONArray array) {
        List<String> list = Collections.synchronizedList(new ArrayList<>());
        if (validator.isEmpty(array)) {
            String id = id();
            list.add(id);

            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("tag", "text");
            object.put("text", new JSONArray());
            lines.put(id, object);
        } else {
            for (int i = 0, size = array.size(); i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("id");
                if (validator.isEmpty(id)) {
                    id = id();
                    object.put("id", id);
                }
                list.add(id);
                lines.put(id, object);
            }
        }
        ids.put(key, list);
    }

    private String id() {
        for (int i = 0; i < 1024; i++) {
            String id = generator.random(16);
            if (!lines.containsKey(id))
                return id;
        }

        return generator.random(16);
    }

    @Override
    public JSONArray get(String key) {
        JSONArray array = new JSONArray();
        if (!ids.containsKey(key)) {
            if (key.equals("editor"))
                put("editor", null);
            else
                return array;
        }

        for (String id : ids.get(key))
            array.add(lines.get(id));

        return array;
    }
}
