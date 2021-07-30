package org.lpw.clivia.group.member;

import org.lpw.photon.dao.orm.PageList;

interface MemberDao {
    PageList<MemberModel> query(String group);

    PageList<MemberModel> query(String user, int type);

    MemberModel find(String group, String user);

    void save(MemberModel member);
}