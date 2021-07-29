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
    public PageList<MemberModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(MemberModel.class).size(pageSize).page(pageNum), null);
    }

    @Override
    public MemberModel findById(String id) {
        return liteOrm.findById(MemberModel.class, id);
    }

    @Override
    public void save(MemberModel member) {
        liteOrm.save(member);
    }
}