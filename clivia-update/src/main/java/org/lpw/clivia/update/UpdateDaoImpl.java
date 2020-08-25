package org.lpw.clivia.update;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(UpdateModel.NAME + ".dao")
class UpdateDaoImpl implements UpdateDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<UpdateModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(UpdateModel.class).order("c_version desc").size(pageSize).page(pageNum), null);
    }

    @Override
    public UpdateModel findById(String id) {
        return liteOrm.findById(UpdateModel.class, id);
    }

    @Override
    public UpdateModel latest(int version, int client) {
        return liteOrm.findOne(new LiteQuery(UpdateModel.class).where("c_version>? and c_client=?").order("c_version desc"), new Object[]{version, client});
    }

    @Override
    public void save(UpdateModel update) {
        liteOrm.save(update);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(UpdateModel.class, id);
    }
}
