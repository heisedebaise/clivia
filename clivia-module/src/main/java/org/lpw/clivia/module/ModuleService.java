package org.lpw.clivia.module;

import com.alibaba.fastjson.JSONObject;

public interface ModuleService {
    String VALIDATOR_NAME_NOT_EXISTS = ModuleModel.NAME + ".validator.name.not-exists";

    JSONObject query();

    void save(ModuleModel module);

    void delete(String id);
}
