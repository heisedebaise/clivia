package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONObject;

public interface MemberService {
    JSONObject query();

    void save(String id, String content);
}
