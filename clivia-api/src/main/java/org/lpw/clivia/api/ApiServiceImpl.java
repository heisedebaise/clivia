package org.lpw.clivia.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        for (int i = 0, size = children.size(); i < size; i++) {
            JSONObject child = children.getJSONObject(i);
            message(child, "name", name, child.getString("uri"));
            if (child.containsKey("parameters")) {
                JSONArray parameters = child.getJSONArray("parameters");
                for (int j = 0, s = parameters.size(); j < s; j++) {
                    JSONObject parameter = parameters.getJSONObject(j);
                    message(parameter, "description", name, parameter.getString("name"));
                }
            }
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

    private void message(JSONObject object, String name, String prefix, String defaultName) {
        String key = defaultName;
        if (object.containsKey(name))
            key = object.getString(name);
        int index = key.indexOf('.');
        if (index == -1)
            key = prefix + "." + key;
        else if (index == 0)
            key = prefix + key;
        if (!validator.isEmpty(key))
            object.put(name, message.get(key));
    }
}
