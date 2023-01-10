package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface MemberService {
    JSONObject query();

    void save(String id, String content);

    void empty(Timestamp time);
}
