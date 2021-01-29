package org.lpw.clivia.user.stat;

import com.alibaba.fastjson.JSONObject;

public interface StatService {
    JSONObject query(String date);

    JSONObject today();
}
