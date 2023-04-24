package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(EditorModel.NAME + ".service")
public class EditorServiceImpl implements EditorService, ContextRefreshedListener, SecondsJob, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Session session;
    private final Map<String, EditorListener> listeners = new HashMap<>();
    private final Map<String, Editing> map = new ConcurrentHashMap<>();

    @Override
    public JSONArray get(String listener, String key) {
        String k = listener + ":" + key;
        if (map.containsKey(k))
            return map.get(k).get();

        if (!listeners.containsKey(listener))
            return new JSONArray();

        EditorListener el = listeners.get(listener);
        JSONArray array = el.get(key);
        map.put(k, new Editing(validator, generator, key, array, el));

        return array;
    }

    @Override
    public JSONObject put(String listener, String key, String id, JSONArray lines, long sync) {
        String k = listener + ":" + key;
        if (map.containsKey(k))
            return map.get(k).put(id, lines, sync);

        return new JSONObject();
    }

    @Override
    public int getContextRefreshedSort() {
        return 114;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(EditorListener.class).forEach(listener -> listeners.put(listener.name(), listener));
    }

    @Override
    public void executeSecondsJob() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.SECOND) % 5 > 0)
            return;

        long time = calendar.getTimeInMillis() - TimeUnit.Minute.getTime();
        map.values().forEach(editing -> editing.save(time));
    }

    @Override
    public void executeHourJob() {
        long time = System.currentTimeMillis() - TimeUnit.Day.getTime();
        map.forEach((key, editing) -> {
            if (editing.overdue(time))
                map.remove(key);
        });
    }
}
