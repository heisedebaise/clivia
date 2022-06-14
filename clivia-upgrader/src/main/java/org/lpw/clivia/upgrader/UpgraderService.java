package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONObject;

public interface UpgraderService {
    JSONObject query();

    JSONObject latest(String client);

    void save(UpgraderModel upgrader);

    void delete(String id);
}
