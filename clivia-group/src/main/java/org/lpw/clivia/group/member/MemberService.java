package org.lpw.clivia.group.member;

import com.alibaba.fastjson.JSONObject;

public interface MemberService {

    JSONObject user();

    void save(String group, String[] users);
}
