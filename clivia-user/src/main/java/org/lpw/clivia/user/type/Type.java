package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;

/**
 * 认证类型。
 *
 * @author lpw
 */
public interface Type {
    /**
     * 获取类型KEY。
     *
     * @return 类型KEY。
     */
    String getKey();

    /**
     * 认证。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return 认证通过则返回用户信息；否则返回null。
     */
    UserModel auth(String uid, String password);

    /**
     * 获取UID。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return UID，如果获取失败则返回null。
     */
    String[] getUid(String uid, String password);

    /**
     * 获取第三方认证手机号。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return 手机号，不存在则返回null。
     */
    String getMobile(String uid, String password);

    /**
     * 获取第三方认证Email。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return Email，不存在则返回null。
     */
    String getEmail(String uid, String password);

    /**
     * 获取第三方认证昵称。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return 昵称，不存在则返回null。
     */
    String getNick(String uid, String password);

    /**
     * 获取第三方头像URL。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return 头像URL，不存在则返回null。
     */
    String getPortrait(String uid, String password);

    /**
     * 获取第三方认证信息。
     *
     * @param uid      UID。
     * @param password 密码。
     * @return 认证信息，不存在则返回null。
     */
    JSONObject getAuth(String uid, String password);
}
