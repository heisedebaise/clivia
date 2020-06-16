package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.crosier.CrosierValid;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private Converter converter;
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
            if (meta.containsKey("props"))
                setLabel(all, false, uri, prefix, meta.getJSONArray("props"), ln);
            for (String mk : meta.keySet()) {
                if (kup.contains(mk))
                    continue;

                JSONObject object = meta.getJSONObject(mk);
                JSONArray props = props(meta, object);
                if (!validator.isEmpty(props))
                    object.put("props", props);
                setLabel(all, false, uri, prefix, props, ln);
                if (object.containsKey("search"))
                    setLabel(true, false, uri, prefix, object.getJSONArray("search"), ln);
                for (String action : actions)
                    if (object.containsKey(action))
                        setLabel(all, true, uri, prefixOp, object.getJSONArray(action), lst);
            }

            return meta;
        }, false);
    }

    private void setLabel(boolean all, boolean type, String uri, String[] prefix, JSONArray array, String[] k) {
        if (validator.isEmpty(array))
            return;

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
        String labels;
        if (object.containsKey("labels") && (labels=object.getString("labels")).indexOf('[') == -1) {
            JSONArray array = new JSONArray();
            array.addAll(Arrays.asList(converter.toArray(getMessage(prefix[0], labels), ",")));
            object.put("labels", array);
        } else if (object.containsKey("values")) {
            JSONObject values = object.getJSONObject("values");
            for (String k : values.keySet())
                values.put(k, getMessage(prefix[0], values.getString(k)));
        }
        if (object.containsKey("message"))
            object.put("message", getMessage(prefix[0], object.getString("message")));

        for (String k : key) {
            if (!object.containsKey(k))
                continue;

            for (String p : prefix) {
                String label = object.getString(k);
                String message = getMessage(p, label);
                if (!message.endsWith(label)) {
                    object.put("label", message);

                    return;
                }
            }
        }
        object.put("label", "");
    }

    private String getMessage(String prefix, String key) {
        int index = key.indexOf('.');
        if (index == -1)
            key = prefix + "." + key;
        else if (index == 0)
            key = prefix + key;

        return message.get((key));
    }

    @Override
    public JSONArray props(JSONObject meta, JSONObject m) {
        if (!json.containsKey(m, "props"))
            return json.containsKey(meta, "props") ? meta.getJSONArray("props") : null;

        JSONArray props = m.getJSONArray("props");
        if (!json.containsKey(meta, "props"))
            return props;

        JSONArray array = new JSONArray();
        JSONArray ps = meta.getJSONArray("props");
        for (int i = 0, size = props.size(); i < size; i++) {
            JSONObject prop = props.getJSONObject(i);
            if (prop.containsKey("name")) {
                for (int j = 0, s = ps.size(); j < s; j++) {
                    JSONObject p = ps.getJSONObject(j);
                    if (prop.getString("name").equals(p.getString("name"))) {
                        JSONObject object = json.copy(p);
                        object.putAll(prop);
                        prop = object;

                        break;
                    }
                }
            }
            array.add(prop);
        }

        return array;
    }

    @Override
    public int getContextRefreshedSort() {
        return 191;
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
        if (object.containsKey("props")) {
            upload(prefix, "image", object.getJSONArray("props"));
            upload(prefix, "file", object.getJSONArray("props"));
        }
        for (String key : object.keySet()) {
            if (kup.contains(key))
                continue;

            JSONObject obj = object.getJSONObject(key);
            if (json.containsKey(obj, "props")) {
                upload(prefix, "image", obj.getJSONArray("props"));
                upload(prefix, "file", obj.getJSONArray("props"));
            }
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
