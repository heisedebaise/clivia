package org.lpw.clivia.upgrader;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(UpgraderModel.NAME + ".dao")
class UpgraderDaoImpl implements UpgraderDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<UpgraderModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(UpgraderModel.class).order("c_version desc").size(pageSize).page(pageNum), null);
    }

    @Override
    public UpgraderModel findById(String id) {
        return liteOrm.findById(UpgraderModel.class, id);
    }

    @Override
    public UpgraderModel latest(int version, int client) {
        return liteOrm.findOne(new LiteQuery(UpgraderModel.class).where("c_version>? and c_client=?").order("c_version desc"), new Object[]{version, client});
    }

    @Override
    public void save(UpgraderModel upgrader) {
        liteOrm.save(upgrader);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(UpgraderModel.class, id);
    }
}
