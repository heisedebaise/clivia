package org.lpw.clivia.chat;

import com.alibaba.fastjson.JSONObject;

public interface ChatService {
    JSONObject query(String group, long time);

    void save(String group, String sender, String genre, String body);
}
