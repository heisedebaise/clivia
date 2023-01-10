package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface MemberService {
    JSONObject query();

    MemberModel findById(String id);

    void save(String id, String content);

    void userRead(String id, Timestamp time);

    void replierRead(String id, Timestamp time);

    void empty(Timestamp time);
}
