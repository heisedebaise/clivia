package org.lpw.clivia.group;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(GroupModel.NAME + ".dao")
class GroupDaoImpl implements GroupDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<GroupModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(GroupModel.class).size(pageSize).page(pageNum), null);
    }
}