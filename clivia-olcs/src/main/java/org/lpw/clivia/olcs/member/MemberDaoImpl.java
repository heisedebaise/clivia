package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(MemberModel.NAME + ".dao")
class MemberDaoImpl implements MemberDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<MemberModel> query() {
        return liteOrm.query(new LiteQuery(MemberModel.class).order("c_time desc"), null);
    }

    @Override
    public MemberModel findById(String id) {
        return liteOrm.findById(MemberModel.class, id);
    }

    @Override
    public void insert(MemberModel member) {
        liteOrm.insert(member);
    }

    @Override
    public void save(MemberModel member) {
        liteOrm.save(member);
    }

    @Override
    public void content(String content, Timestamp time) {
        liteOrm.update(new LiteQuery(MemberModel.class).set("c_content=?").where("c_time<?"), new Object[]{content, time});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(MemberModel.class, id);
    }
}