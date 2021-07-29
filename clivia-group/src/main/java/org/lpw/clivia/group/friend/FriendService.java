package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;

public interface FriendService {
    JSONObject user();

    void add(String user, String memo);

    void agree(String id);

    void reject(String id);
}
