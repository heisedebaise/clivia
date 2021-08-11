package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONObject;

public interface GroupService {
    JSONObject friends();

    JSONObject find(String idUidCode);

    GroupModel friend(String[] users);

    void cleanFriendsCache(String user);
}
