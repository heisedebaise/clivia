package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONArray;

import java.sql.Timestamp;

public interface OlcsService {
    JSONArray query(String user, Timestamp time);

    JSONArray user(Timestamp time);

    void ask(String genre, String content);

    void reply(String user, String genre, String content);
}
