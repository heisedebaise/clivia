package org.lpw.clivia.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;

public interface AliyunService {
    String KEY_NOT_EXISTS_VALIDATOR = AliyunModel.NAME + ".validator.key.not-exists";

    JSONObject query(String key);

    void save(AliyunModel aliyun);

    void delete(String id);

    Config config(AliyunModel aliyun);
}
