package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(EditorModel.NAME + ".service")
public class EditorServiceImpl implements EditorService {
    @Inject
    private Json json;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Session session;
    @Inject
    private EditorDao editorDao;
    private final Map<String, Map<String, JSONArray>> map = new ConcurrentHashMap<>();

    @Override
    public String newKey() {
        for (int i = 0; i < 1024; i++) {
            String key = generator.random(32);
            if (editorDao.findByKey(key) == null)
                return key;
        }

        return null;
    }

    @Override
    public void putKey(String key) {
        map.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(session.getId(), new JSONArray());
    }

    @Override
    public JSONArray query(String key) {
        JSONArray array = new JSONArray();
        if (!map.containsKey(key))
            return array;

        for (EditorModel editor : editorDao.query(key).getList())
            array.add(json.toObject(editor.getContent()));

        return array;
    }

    @Override
    public JSONObject save(String key, JSONObject content) {
        if (!map.containsKey(key))
            return content;

        EditorModel editor = editorDao.findById(content.getString("id"));
        boolean isNull = editor == null;
        if (isNull) {
            editor = new EditorModel();
            editor.setId(generator.uuid());
            content.put("id", editor.getId());
        }
        editor.setKey(key);
        editor.setOrder(content.getIntValue("order"));
        editor.setTime(System.currentTimeMillis());
        content.put("time", editor.getTime());
        editor.setContent(json.toString(content));
        if (isNull) {
            editorDao.insert(editor);
            order(key);
            content.put("action", "create");
        } else {
            editorDao.save(editor, false);
            content.put("action", "modify");
        }
        push(key, content);

        return content;
    }

    @Override
    public void order(String id, int order) {
        EditorModel editor = editorDao.findById(id);
        if (editor == null || editor.getOrder() == order || !map.containsKey(editor.getKey()))
            return;

        editor.setOrder(order);
        editor.setTime(System.currentTimeMillis());
        JSONObject content = json.toObject(editor.getContent());
        content.put("order", order);
        content.put("time", editor.getTime());
        editor.setContent(json.toString(content));
        editorDao.save(editor, true);
        order(editor.getKey());
        push(editor.getKey(), "order", id);
    }

    @Override
    public void delete(String id) {
        EditorModel editor = editorDao.findById(id);
        if (editor == null || !map.containsKey(editor.getKey()))
            return;

        editorDao.delete(id);
        order(editor.getKey());
        push(editor.getKey(), "delete", id);
    }

    private void order(String key) {
        List<EditorModel> list = editorDao.query(key).getList();
        long time = System.currentTimeMillis();
        for (int i = 0, size = list.size(); i < size; i++) {
            EditorModel editor = list.get(i);
            if (editor.getOrder() == i)
                continue;

            editor.setOrder(i);
            editor.setTime(time);
            JSONObject object = json.toObject(editor.getContent());
            object.put("order", i);
            object.put("time", time);
            editor.setContent(json.toString(object));
            editorDao.save(editor, false);
        }
    }

    private void push(String key, String action, String id) {
        JSONObject object = new JSONObject();
        object.put("action", action);
        object.put("id", id);
        push(key, object);
    }

    private void push(String key, JSONObject object) {
        Map<String, JSONArray> map = this.map.get(key);
        if (map.size() <= 1)
            return;

        String sid = session.getId();
        map.forEach((s, array) -> {
            if (!s.equals(sid))
                array.add(object);
        });
    }
}
