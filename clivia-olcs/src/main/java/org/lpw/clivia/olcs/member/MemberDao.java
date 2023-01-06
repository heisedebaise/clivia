package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.orm.PageList;

interface MemberDao {
    PageList<MemberModel> query();

    MemberModel findById(String id);

    void insert(MemberModel member);

    void save(MemberModel member);

    void delete(String id);
}