package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface MemberDao {
    PageList<MemberModel> query();

    MemberModel findById(String id);

    void insert(MemberModel member);

    void save(MemberModel member);

    void content(String content, Timestamp time);

    void delete(String id);
}