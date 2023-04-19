package org.lpw.clivia.group.member;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(MemberModel.NAME + ".dao")
class MemberDaoImpl implements MemberDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<MemberModel> query(String group) {
        return liteOrm.query(new LiteQuery(MemberModel.class).where("c_group=?").order("c_grade desc,c_time"), new Object[]{group});
    }

    @Override
    public PageList<MemberModel> query(String user, int type) {
        if (type == -1)
            return liteOrm.query(new LiteQuery(MemberModel.class).where("c_user=? and c_state<=?"), new Object[]{user, 1});

        return liteOrm.query(new LiteQuery(MemberModel.class).where("c_user=? and c_type=? and c_state<=?"), new Object[]{user, type, 1});
    }

    @Override
    public MemberModel findById(String id) {
        return liteOrm.findById(MemberModel.class, id);
    }

    @Override
    public MemberModel find(String group, String user) {
        return liteOrm.findOne(new LiteQuery(MemberModel.class).where("c_user=? and c_group=?"),
                new Object[]{user, group});
    }

    @Override
    public int count(String group) {
        return liteOrm.count(new LiteQuery(MemberModel.class).where("c_group=? and c_state<=?"), new Object[]{group, 1});
    }

    @Override
    public void save(MemberModel member) {
        liteOrm.save(member);
    }

    @Override
    public void state(String group, int oldState, int newState) {
        liteOrm.update(new LiteQuery(MemberModel.class).set("c_state=?").where("c_group=? and c_state=?"), new Object[]{newState, group, oldState});
    }

    @Override
    public void delete(String group) {
        liteOrm.delete(new LiteQuery(MemberModel.class).where("c_group=?"), new Object[]{group});
    }

    @Override
    public void delete(String group, String user) {
        liteOrm.delete(new LiteQuery(MemberModel.class).where("c_user=? and c_group=?"), new Object[]{user, group});
    }
}