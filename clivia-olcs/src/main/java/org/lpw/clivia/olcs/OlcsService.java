package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONObject;

public interface OlcsService {
    JSONObject users();

    void ask(String genre, String content);

    void reply(String user, String genre, String content);
}
