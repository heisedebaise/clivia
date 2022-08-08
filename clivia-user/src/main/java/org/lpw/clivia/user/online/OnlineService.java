package org.lpw.clivia.user.online;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;

import java.sql.Date;

public interface OnlineService {
    /**
     * 检索在线数据集。
     *
     * @param uid 认证ID。
     * @param ip  IP地址。
     * @return 在线数据集。
     */
    JSONObject query(String uid, String ip);

    /**
     * 根据SID获取在线信息。
     *
     * @param sid SID。
     * @return 在线信息。
     */
    OnlineModel findBySid(String sid);

    /**
     * 登入。
     *
     * @param user 用户。
     */
    void signIn(UserModel user);

    /**
     * 访问。
     */
    void visit();

    /**
     * 登出。
     */
    void signOut();

    /**
     * 强制登出。
     *
     * @param id ID值。
     */
    void signOutId(String id);

    /**
     * 强制登出。
     *
     * @param sid Session ID。
     */
    void signOutSid(String sid);

    /**
     * 强制登出。
     *
     * @param user 用户ID值。
     */
    void signOutUser(String user);

    /**
     * 统计在线用户数。
     *
     * @param date 日期。
     * @return 在线用户数。
     */
    int count(Date date);
}
