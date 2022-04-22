package org.lpw.clivia.sms.huaweiyun;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.lpw.clivia.sms.SmsPusher;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

@Service("clivia.sms.huaweiyun.pusher")
public class SmsPusherImpl implements SmsPusher {
    @Inject
    private Json json;
    @Inject
    private Generator generator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Digest digest;
    @Inject
    private Codec codec;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Http http;
    @Inject
    private Logger logger;

    @Override
    public String key() {
        return "huawei";
    }

    @Override
    public String name() {
        return "clivia.sms.huaweiyun.name";
    }

    @Override
    public Object push(String config, String mobile, String content) {
        JSONObject cfg = json.toObject(config);
        if (!validator.isMobile(mobile) || cfg == null || !cfg.containsKey("url") || !cfg.containsKey("key")
                || !cfg.containsKey("secret") || !cfg.containsKey("from") || !cfg.containsKey("templateId")
                || !cfg.containsKey("signature"))
            return null;

        String nonce = generator.random(16);
        String time = dateTime.toString(dateTime.now(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        String string = http.post(cfg.getString("url") + "/sms/batchSendSms/v1",
                Map.of("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                        "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"", "X-WSSE",
                        String.format("UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"",
                                cfg.getString("key"),
                                codec.encodeBase64(
                                        digest.digest("sha256", (nonce + time + cfg.getString("secret")).getBytes())),
                                nonce, time)),
                Map.of("from", cfg.getString("from"), "to", mobile, "templateId", cfg.getString("templateId"),
                        "templateParas", paras(content), "signature", cfg.getString("signature")));
        if (logger.isInfoEnable())
            logger.info("发送华为云短信[{}:{}:{}:{}]。", config, mobile, content, string);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("code"))
            return null;

        if (json.has(object, "code", "000000"))
            return "";

        JSONObject obj = new JSONObject();
        obj.put("code", 108999);
        obj.put("message", string);

        return obj;
    }

    private String paras(String content) {
        if (validator.isEmpty(content))
            return "";

        JSONArray array = new JSONArray();
        Collections.addAll(array, converter.toArray(content, ","));

        return array.toJSONString();
    }
}
