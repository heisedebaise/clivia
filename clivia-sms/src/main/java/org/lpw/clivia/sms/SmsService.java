package org.lpw.clivia.sms;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface SmsService {
    JSONObject query(String scene, String pusher, String name, int state);

    void save(SmsModel sms);

    void state(String id, int state);

    void delete(String id);
}
