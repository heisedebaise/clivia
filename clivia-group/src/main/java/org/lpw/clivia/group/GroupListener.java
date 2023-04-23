package org.lpw.clivia.group;

import org.lpw.clivia.group.member.MemberModel;

import java.util.List;

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
     * 成员加入。
     *
     * @param group  群组。
     * @param member 成员。
     */
    void groupJoin(GroupModel group, MemberModel member);

    /**
     * 退出群。
     *
     * @param group  群组。
     * @param member 退出成员。
     * @param audit  是否管理员移除。
     */
    void groupExit(GroupModel group, MemberModel member, boolean audit);

    /**
     * 删除群组。
     *
     * @param group   群组。
     * @param members 群成员集。
     */
    void groupDelete(GroupModel group, List<MemberModel> members);
}
