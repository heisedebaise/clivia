package org.lpw.clivia.wps;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface WpsService {
    enum Permission {ReadOnly, ReadWrite}

    String VALIDATOR_KEY_NOT_EXISTS = WpsModel.NAME + ".validator.key.not-exists";

    JSONObject query(String key, String name, String appId);

    WpsModel findById(String id);

    void save(WpsModel wps);

    void delete(String id);

    String preview(String key, String uri, String name, Permission permission, String creator, long create);
}
