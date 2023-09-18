package org.lpw.clivia.user.info;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Set;

public interface InfoService {
    JSONObject user();

    JSONObject get(String user);

    JSONObject get(String user, String name);

    Set<String> users(Map<String, String> map);

    void save(String name, String value);

    void save(String user, String name, String value);

    void save(Map<String, String> map);

    void delete(String user);
}
