package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONObject;

public interface GroupService {
    JSONObject friends();

    JSONObject find(String idUidCode);

    void friend(String[] users);
}
