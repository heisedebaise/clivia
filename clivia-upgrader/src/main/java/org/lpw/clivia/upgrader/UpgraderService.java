package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONObject;

public interface UpgraderService {
    JSONObject query();

    JSONObject latest(int client);

    void save(UpgraderModel upgrader);

    void delete(String id);
}
