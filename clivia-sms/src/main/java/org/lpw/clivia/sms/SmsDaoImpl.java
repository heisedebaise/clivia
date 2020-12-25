package org.lpw.clivia.sms;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

/**
 * @author lpw
 */
@Repository(SmsModel.NAME + ".dao")
class SmsDaoImpl implements SmsDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<SmsModel> query(String scene, String pusher, String name, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_pusher", DaoOperation.Equals, pusher)
                .where("c_state", DaoOperation.Equals, state)
                .like(null, "c_scene", scene)
                .like(null, "c_name", name)
                .order("c_time desc")
                .query(SmsModel.class, pageSize, pageNum);
    }

    @Override
    public SmsModel findById(String id) {
        return liteOrm.findById(SmsModel.class, id);
    }

    @Override
    public void save(SmsModel sms) {
        liteOrm.save(sms);
    }

    @Override
    public void state(String id, int state) {
        liteOrm.update(new LiteQuery(SmsModel.class).set("c_state=?").where("c_id=?"), new Object[]{state, id});
    }

    @Override
    public void time(String id, Timestamp time) {
        liteOrm.update(new LiteQuery(SmsModel.class).set("c_time=?").where("c_id=?"), new Object[]{time, id});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(SmsModel.class, id);
    }
}
