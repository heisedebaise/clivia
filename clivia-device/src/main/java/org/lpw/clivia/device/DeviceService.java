package org.lpw.clivia.device;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface DeviceService {
    JSONObject query();

    JSONArray user();

    DeviceModel find(String sid);

    void save(String type, String identifier, String description, String lang);

    void offline(String id);
}
