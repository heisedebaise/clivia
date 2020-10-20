package org.lpw.clivia.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.model.ModelTables;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Service(ApiModel.NAME + ".service")
public class ApiServiceImpl implements ApiService, ContextRefreshedListener {
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Message message;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Inject
    private Optional<Set<Model>> models;
    private JSONArray array;

    @Override
    public JSONArray get() {
        return array;
    }

    @Override
    public int getContextRefreshedSort() {
        return 192;
    }

    @Override
    public void onContextRefreshed() {
        List<JSONObject> list = new ArrayList<>();
        models.ifPresent(set -> set.forEach(model -> {
            Class<? extends Model> modelClass = model.getClass();
            try (InputStream inputStream = modelClass.getResourceAsStream("api.json");
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (inputStream == null)
                    return;

                io.copy(inputStream, outputStream);
                put(list, modelClass, outputStream.toString());
            } catch (Throwable throwable) {
                logger.warn(throwable, "解析API[{}]元数据时发生异常！", modelClass);
            }
        }));
        list.sort(Comparator.comparingInt(object -> object.getIntValue("sort")));
        array = new JSONArray();
        array.addAll(list);
        if (logger.isInfoEnable())
            logger.info("API配置集{}", array);
    }

    private void put(List<JSONObject> list, Class<? extends Model> modelClass, String string) {
        JSONObject object = json.toObject(string);
        if (object == null)
            return;

        String name = modelTables.get(modelClass).getName();
        if (!object.containsKey("sort"))
            object.put("sort", getCode(name));
        message(object, "name", "", name);
        JSONArray children = object.getJSONArray("children");
        String uri = object.getString("uri");
        for (int i = 0, size = children.size(); i < size; i++) {
            JSONObject child = children.getJSONObject(i);
            String u = child.getString("uri");
            child.put("uri", uri + u);
            message(child, "name", name, u);
            description(child, "headers", name);
            psid(child);
            description(child, "parameters", name);
            response(child, name);
        }
        if (object.containsKey("model")) {
            response(object, "model", name);
            format(object, "model");
        }
        list.add(object);
    }

    private int getCode(String name) {
        Object ctrl = BeanFactory.getBean(name + ".ctrl");
        if (ctrl == null)
            return 999;

        Execute execute = ctrl.getClass().getAnnotation(Execute.class);
        if (execute == null)
            return 999;

        return numeric.toInt(execute.code());
    }

    private void description(JSONObject object, String name, String prefix) {
        if (!object.containsKey(name))
            return;

        JSONArray parameters = object.getJSONArray(name);
        for (int j = 0, s = parameters.size(); j < s; j++) {
            JSONObject parameter = parameters.getJSONObject(j);
            message(parameter, "description", prefix, "." + parameter.getString("name") + ".description");
        }
    }

    private void message(JSONObject object, String name, String prefix, String defaultName) {
        String key = defaultName;
        if (object.containsKey(name))
            key = object.getString(name);
        object.put(name, getMessage(prefix, key));
    }

    private void psid(JSONObject object) {
        if (!json.hasTrue(object, "psid"))
            return;

        object.remove("psid");
        JSONArray headers = object.containsKey("headers") ? object.getJSONArray("headers") : new JSONArray();
        JSONObject psid = new JSONObject();
        psid.put("name", "photon-session-id");
        psid.put("type", "string");
        psid.put("require", true);
        psid.put("description", message.get(ApiModel.NAME + ".psid.description"));
        headers.add(psid);
        object.put("headers", headers);
    }

    private void response(JSONObject object, String prefix) {
        if (object.containsKey("response")) {
            if (response(object, "response", prefix) > 0)
                format(object, "response");
        } else
            object.put("response", "\"\"");
    }

    private int response(JSONObject object, String name, String prefix) {
        Object value = object.get(name);
        if (value.equals("model") || value.equals("page"))
            return 0;

        if (value instanceof JSONArray) {
            object.put(name, response((JSONArray) value, name, prefix));

            return 1;
        }

        if (value instanceof JSONObject) {
            JSONObject obj = (JSONObject) value;
            obj.keySet().forEach(key -> response(obj, key, prefix));

            return 2;
        }

        if (name.equals("id")) {
            object.put(name, message.get(ApiModel.NAME + ".id.description"));

            return 0;
        }

        String description = value.toString();
        object.put(name, getMessage(prefix, validator.isEmpty(description) ? ("." + name + ".description") : description));

        return 0;
    }

    private JSONArray response(JSONArray array, String name, String prefix) {
        JSONArray newArray = new JSONArray();
        array.forEach(object -> {
            if (object instanceof JSONObject) {
                JSONObject obj = (JSONObject) object;
                obj.keySet().forEach(key -> response(obj, key, prefix));
                newArray.add(obj);
            } else {
                String description = object.toString();
                newArray.add(getMessage(prefix, validator.isEmpty(description) ? ("." + name + ".description") : description));
            }
        });

        return newArray;
    }

    private String getMessage(String prefix, String key) {
        int index = key.indexOf('.');
        if (index == -1)
            key = prefix + "." + key;
        else if (index == 0)
            key = prefix + key;

        return validator.isEmpty(key) ? "" : message.get(key);
    }

    private void format(JSONObject object, String name) {
        JSON json = object.getObject(name, JSON.class);
        object.put(name, json.toString(SerializerFeature.PrettyFormat).replaceAll("\t", "    "));
    }
}
