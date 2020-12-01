package org.lpw.clivia.user;

/**
 * 用户监听器。
 *
 * @author lpw
 */
public interface UserListener {
    /**
     * 新用户注册。
     *
     * @param user 用户。
     */
    void userSignUp(UserModel user);

    /**
     * 用户被删除。
     *
     * @param user       用户。
     * @param completely 是否彻底删除。
     */
    void userDeleted(UserModel user, boolean completely);
}
