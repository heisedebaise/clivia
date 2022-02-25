package org.lpw.clivia.user;

/**
 * 用户监听器。
 */
public interface UserListener {
    /**
     * 新用户注册。
     *
     * @param user 用户。
     */
    void userSignUp(UserModel user);

    /**
     * 用户删除。
     *
     * @param user 用户。
     */
    void userDelete(UserModel user);
}
