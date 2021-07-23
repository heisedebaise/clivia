package org.lpw.clivia.group;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import java.util.Set;

import javax.inject.Inject;

@Repository(GroupModel.NAME + ".dao")
class GroupDaoImpl implements GroupDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<GroupModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(GroupModel.class).size(pageSize).page(pageNum), null);
    }

    @Override
    public PageList<GroupModel> query(Set<String> ids) {
        return daoHelper.newQueryBuilder().in("c_id", ids).query(GroupModel.class, 0, 0);
    }
}