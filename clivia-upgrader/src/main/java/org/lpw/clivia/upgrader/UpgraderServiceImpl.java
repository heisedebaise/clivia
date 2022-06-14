package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(UpgraderModel.NAME + ".service")
public class UpgraderServiceImpl implements UpgraderService {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UpgraderDao upgraderDao;
    @Value("${" + UpgraderModel.NAME + ".latest:}")
    private String latest;
    private final String[] names = {"android", "ios", "windows", "macos", "linux", "url"};

    @Override
    public JSONObject query() {
        return upgraderDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(String client) {
        if (!validator.isEmpty(latest)) {
            Map<String, String> map = new HashMap<>();
            if (!validator.isEmpty(client))
                map.put("client", client);
            String string = http.post(latest + "/upgrader/latest", null, map);
            JSONObject object = json.toObject(string);
            if (!json.has(object, "code", "0")) {
                logger.warn(null, "获取远端最新版本[{}:{}]信息失败！", client, string);

                return new JSONObject();
            }

            object = object.getJSONObject("data");
            for (String name : names) {
                if (!object.containsKey(name))
                    continue;

                String value = object.getString(name);
                if (!value.contains("://"))
                    object.put(name, latest + value);
            }

            return object;
        }

        if (validator.isEmpty(client)) {
            UpgraderModel upgrader = upgraderDao.latest();
            if (upgrader == null)
                return new JSONObject();

            JSONObject object = modelHelper.toJson(upgrader);
            object.put("explain", json.toObject(upgrader.getExplain()));

            return object;
        }

        JSONObject object = new JSONObject();
        for (UpgraderModel upgrader : upgraderDao.query(0, 0).getList()) {
            String url = switch (client) {
                case "android", "0" -> upgrader.getAndroid();
                case "ios", "1" -> upgrader.getIos();
                case "windows", "2" -> upgrader.getWindows();
                case "macos", "3" -> upgrader.getMacos();
                case "linux", "4" -> upgrader.getLinux();
                default -> "";
            };
            if (validator.isEmpty(url))
                continue;

            object.put("version", upgrader.getVersion());
            object.put("name", upgrader.getName());
            object.put("explain", json.toObject(upgrader.getExplain()));
            object.put("url", url);
            break;
        }

        return object;
    }

    @Override
    public void save(UpgraderModel upgrader) {
        if (validator.isEmpty(upgrader.getId()) || upgraderDao.findById(upgrader.getId()) == null)
            upgrader.setId(null);
        JSONArray array = json.toArray(upgrader.getFile());
        if (!validator.isEmpty(array)) {
            int size = array.size();
            for (int i = 0; i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                if (validator.isEmpty(upgrader.getAndroid()) && name.contains("android"))
                    upgrader.setAndroid(object.getString("uri"));
                if (validator.isEmpty(upgrader.getWindows()) && name.contains("windows"))
                    upgrader.setWindows(object.getString("uri"));
                if (validator.isEmpty(upgrader.getMacos()) && name.contains("macos"))
                    upgrader.setMacos(object.getString("uri"));
                if (validator.isEmpty(upgrader.getLinux()) && name.contains("linux"))
                    upgrader.setLinux(object.getString("uri"));
            }
        }
        upgraderDao.save(upgrader);
    }

    @Override
    public void delete(String id) {
        upgraderDao.delete(id);
    }
}
