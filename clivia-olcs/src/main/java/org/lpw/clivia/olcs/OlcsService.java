package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONObject;

public interface OlcsService {

    JSONObject query(String user);

    JSONObject user(String user);

    void ask( String genre, String content);

    void reply(String user, String genre, String content);
}
