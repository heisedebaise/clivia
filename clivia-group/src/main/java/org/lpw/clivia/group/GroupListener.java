package org.lpw.clivia.group;

/**
 * 群组监听器。
 */
public interface GroupListener {
    /**
     * 创建群组。
     *
     * @param group 群组。
     */
    void groupCreate(GroupModel group);

    /**
     * 删除群组。
     *
     * @param groups 删除群组的ID集。
     * @param users  只删除用户的群组ID集。
     * @param user   删除用户ID。
     */
    void groupDelete(String groups, String users, String user);
}
