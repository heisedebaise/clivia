package org.lpw.clivia.user.stat;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface StatService {
    JSONObject query(String date);

    JSONObject today();
}
