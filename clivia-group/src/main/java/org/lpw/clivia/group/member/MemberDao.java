package org.lpw.clivia.group.member;

import org.lpw.photon.dao.orm.PageList;

interface MemberDao {
    PageList<MemberModel> query(int pageSize, int pageNum);

    MemberModel findById(String id);

    void save(MemberModel member);
}