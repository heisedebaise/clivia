package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface UpgraderService {
    JSONObject query();

    JSONObject latest(int version, int client);

    void save(UpgraderModel upgrader);

    void delete(String id);
}
