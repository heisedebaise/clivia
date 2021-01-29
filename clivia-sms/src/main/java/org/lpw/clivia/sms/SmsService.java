package org.lpw.clivia.sms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface SmsService {
    String VALIDATOR_CAPTCHA = SmsModel.NAME + ".validator.captcha";

    JSONObject query(String scene, String pusher, String name, int state);

    JSONArray lvs();

    void save(SmsModel sms);

    void state(String id, int state);

    void delete(String id);

    Object push(String scene, String mobile, String content);

    Object captcha(String scene, String mobile);

    boolean captcha(String code);
}
