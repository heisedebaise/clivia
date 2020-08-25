package org.lpw.clivia.update;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface UpdateService {
    JSONObject query();

    JSONObject latest(int version, int client);

    void save(UpdateModel update);

    void delete(String id);
}
