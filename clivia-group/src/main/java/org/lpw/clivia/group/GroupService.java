package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONObject;

public interface GroupService {
    JSONObject friend();

    GroupModel friend(String[] users);
}
