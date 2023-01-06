package org.lpw.clivia.olcs;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(OlcsModel.NAME + ".dao")
class OlcsDaoImpl implements OlcsDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<OlcsModel> query(String user, Timestamp time) {
        if (time == null)
            return liteOrm.query(new LiteQuery(OlcsModel.class).where("c_user=?").order("c_time"), new Object[]{user});

        return liteOrm.query(new LiteQuery(OlcsModel.class).where("c_user=? and c_time>?").order("c_time"), new Object[]{user, time});
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