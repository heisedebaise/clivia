package org.lpw.clivia.olcs;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(OlcsModel.NAME + ".dao")
class OlcsDaoImpl implements OlcsDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<OlcsModel> query(String user, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_user", DaoOperation.Equals, user)
                .order("c_time DESC")
                .query(OlcsModel.class, pageSize, pageNum);
    }

    @Override
    public OlcsModel findById(String id) {
        return liteOrm.findById(OlcsModel.class, id);
    }

    @Override
    public void save(OlcsModel olcs) {
        liteOrm.save(olcs);
    }
}