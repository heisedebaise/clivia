package org.lpw.clivia.group;

/**
 * 群组监听器。
 */
public interface GroupListener {
    /**
     * 创建群组。
     *
     * @param group    群组。
     * @param prologue 开场白。
     */
    void groupCreate(GroupModel group, String prologue);

    /**
     * 群更新。
     *
     * @param group 群组。
     */
    void groupUpdate(GroupModel group);

    /**
     * 删除群组。
     *
     * @param group 群组。
     */
    void groupDelete(GroupModel group);
}
