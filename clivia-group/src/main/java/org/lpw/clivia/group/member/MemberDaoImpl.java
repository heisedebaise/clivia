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
        return liteOrm.query(new LiteQuery(MemberModel.class).where("c_group=?"), new Object[] { group });
    }

    @Override
    public PageList<MemberModel> query(String user, int type) {
        return liteOrm.query(new LiteQuery(MemberModel.class).where("c_user=? and c_type=? and c_state in(0,1)"),
                new Object[] { user, type });
    }

    @Override
    public MemberModel find(String group, String user) {
        return liteOrm.findOne(new LiteQuery(MemberModel.class).where("c_user=? and c_group=?"),
                new Object[] { user, group });
    }

    @Override
    public void save(MemberModel member) {
        liteOrm.save(member);
    }
}