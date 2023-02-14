package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(EditorModel.NAME + ".service")
public class EditorServiceImpl implements EditorService, SecondsJob {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    private final Map<String, Editing> map = new ConcurrentHashMap<>();

    @Override
    public void put(String key, JSONArray array, EditorListener listener) {
        map.computeIfAbsent(key, k -> new Editing(validator, generator, key, array, listener));
    }

    @Override
    public JSONArray get(String key) {
        if (map.containsKey(key))
            return map.get(key).get();

        if (key.equals("editor")) {
            put(key, null, null);

            return map.get(key).get();
        }

        return new JSONArray();
    }

    @Override
    public JSONObject save(String key, String id, JSONArray lines) {
        if (!map.containsKey(key) || validator.isEmpty(lines))
            return new JSONObject();

        return map.get(key).put(id, lines);
    }

    @Override
    public void executeSecondsJob() {
        long time = System.currentTimeMillis() - TimeUnit.Minute.getTime();
        map.values().forEach(editing -> editing.save(time));
    }
}
