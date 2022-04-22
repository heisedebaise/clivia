package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONObject;

public interface GroupService {
    String VALIDATOR_EXISTS = GroupModel.NAME + ".exists";

    JSONObject get(String id);

    JSONObject members(String id);

    JSONObject friends();

    JSONObject find(String idUidCode);

    String name(String id, String user);

    GroupModel friend(String[] users);

    String self(String user);

    int start(String name, String avatar, String prologue, String[] users);

    void delete(String id);
}
