package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.crosier.CrosierValid;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Service(ConsoleModel.NAME + ".meta.helper")
public class MetaHelperImpl implements MetaHelper, ContextRefreshedListener, CrosierValid {
    @Inject
    private Cache cache;
    @Inject
    private Context context;
    @Inject
    private Message message;
    @Inject
    private Io io;
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private Optional<Set<Model>> models;
    @Inject
    private UserService userService;
    @Inject
    private CrosierService crosierService;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;
    private final Map<String, String> map = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> cacheKeys = new ConcurrentHashMap<>();
    private final Set<String> kup = Set.of("key", "uri", "props");
    private final String[] actions = {"ops", "toolbar"};

    @Override
    public JSONObject get(String key, boolean all) {
        String cacheKey = ConsoleModel.NAME + ".meta:" + key + ":" + all + ":" + userService.grade() + ":" + context.getLocale().toString();
        if (!all)
            cacheKeys.computeIfAbsent(userService.grade(), k -> new HashSet<>()).add(cacheKey);

        return cache.computeIfAbsent(cacheKey, k -> {
            JSONObject meta = json.toObject(map.get(key));
            if (meta == null)
                return new JSONObject();

            String uri = meta.getString("uri");
            String[] prefix = new String[]{meta.getString("key")};
            String[] prefixOp = new String[]{prefix[0], ConsoleModel.NAME + ".op"};
            String[] ln = new String[]{"label", "name"};
            String[] lst = new String[]{"label", "service", "type"};
            setLabel(all, false, uri, prefix, meta, "props", ln);
            for (String mk : meta.keySet()) {
                if (kup.contains(mk))
                    continue;

                JSONObject object = meta.getJSONObject(mk);
                setLabel(all, false, uri, prefix, object, "props", ln);
                setLabel(true, false, uri, prefix, object, "search", ln);
                for (String action : actions)
                    setLabel(all, true, uri, prefixOp, object, action, lst);
            }

            return meta;
        }, false);
    }

    private void setLabel(boolean all, boolean type, String uri, String[] prefix, JSONObject object, String key, String[] k) {
        if (!object.containsKey(key))
            return;

        JSONArray array = object.getJSONArray(key);
        for (int i = array.size() - 1; i >= 0; i--) {
            JSONObject obj = array.getJSONObject(i);
            String service = obj.getString(type && !obj.containsKey("service") ? "type" : "service");
            if (!all && !validator.isEmpty(service)) {
                if (service.charAt(0) != '/')
                    service = uri + service;
                if (!crosierService.permit(service, obj.containsKey("parameter") ? json.toMap(obj.getJSONObject("parameter")) : new HashMap<>())) {
                    if (json.has(obj, "type", "switch"))
                        obj.remove("service");
                    else {
                        array.remove(i);

                        continue;
                    }
                }
            }

            setLabel(prefix, obj, k);
        }
    }

    private void setLabel(String[] prefix, JSONObject object, String[] key) {
        if (object.containsKey("labels")) {
            String labels = object.getString("labels");
            if (labels.charAt(0) == '.')
                labels = prefix[0] + labels;
            JSONArray array = new JSONArray();
            array.addAll(Arrays.asList(message.getAsArray(labels)));
            object.put("labels", array);
        } else if (object.containsKey("values")) {
            JSONObject values = object.getJSONObject("values");
            for (String k : values.keySet()) {
                String v = values.getString(k);
                int index = v.indexOf('.');
                if (index == -1)
                    v = prefix[0] + "." + v;
                else if (index == 0)
                    v = prefix[0] + v;
                values.put(k, message.get(v));
            }
        }

        for (String k : key) {
            for (String p : prefix) {
                if (object.containsKey(k)) {
                    String label = object.getString(k);
                    int index = label.indexOf('.');
                    if (index == -1)
                        label = p + "." + label;
                    else if (index == 0)
                        label = p + label;
                    String msg = message.get(label);
                    if (!msg.equals(label)) {
                        object.put("label", msg);

                        return;
                    }
                }
            }
        }
        object.put("label", "");
    }

    @Override
    public int getContextRefreshedSort() {
        return 11;
    }

    @Override
    public void onContextRefreshed() {
        models.ifPresent(set -> set.forEach(model -> {
            Class<? extends Model> modelClass = model.getClass();
            try (InputStream inputStream = modelClass.getResourceAsStream("meta.json");
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (inputStream == null)
                    return;

                io.copy(inputStream, outputStream);
                put(outputStream.toString());
            } catch (Throwable throwable) {
                logger.warn(throwable, "解析Model[{}]元数据时发生异常！", modelClass);
            }
        }));

        meta(new File(context.getAbsolutePath(console + "meta")).listFiles());
    }

    private void meta(File[] files) {
        if (files == null)
            return;

        for (File file : files) {
            if (file.isDirectory()) {
                meta(file.listFiles());

                continue;
            }

            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;

            put(io.readAsString(file.getAbsolutePath()));
        }
    }

    private void put(String string) {
        JSONObject object = json.toObject(string);
        if (object == null)
            return;

        String prefix = object.getString("key") + ".";
        if (object.containsKey("props"))
            upload(prefix, "image", object.getJSONArray("props"));
        for (String key : object.keySet()) {
            if (kup.contains(key))
                continue;

            JSONObject obj = object.getJSONObject(key);
            if (json.containsKey(obj, "toolbar"))
                upload(prefix, "upload", obj.getJSONArray("toolbar"));
        }

        string = object.toJSONString();
        if (object.containsKey("key"))
            map.put(object.getString("key"), string);
        if (object.containsKey("uri"))
            map.put(object.getString("uri"), string);
    }

    private void upload(String prefix, String type, JSONArray array) {
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject prop = array.getJSONObject(i);
            if (json.has(prop, "type", type)) {
                String upload = prop.containsKey("upload") ? prop.getString("upload") : null;
                if (validator.isEmpty(upload))
                    prop.put("upload", prefix + prop.getString("name"));
                else if (upload.indexOf('.') == -1)
                    prop.put("upload", prefix + upload);
            }
        }
    }

    @Override
    public void crosierValid(int grade) {
        if (cacheKeys.containsKey(grade))
            cacheKeys.remove(grade).forEach(cache::remove);
    }
}
