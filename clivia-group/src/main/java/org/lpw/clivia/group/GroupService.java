package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface GroupService {
    String VALIDATOR_EXISTS = GroupModel.NAME + ".exists";

    JSONArray gets();

    JSONObject get(String id);

    JSONObject friends();

    JSONObject find(String idUidCode);

    GroupModel friend(String[] users);

    void cleanFriendsCache(String user);
}
