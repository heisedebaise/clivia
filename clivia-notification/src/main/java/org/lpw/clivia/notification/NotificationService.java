package org.lpw.clivia.notification;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface NotificationService {
    JSONObject user(String genre);

    JSONObject unread(String[] genre, boolean waitable);

    void save(String user, String genre, String subject, String content, Timestamp expiration);
}
