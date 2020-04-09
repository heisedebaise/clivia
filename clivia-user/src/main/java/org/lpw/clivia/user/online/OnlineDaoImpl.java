package org.lpw.clivia.user.online;

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
@Repository(OnlineModel.NAME + ".dao")
class OnlineDaoImpl implements OnlineDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<OnlineModel> query(String user, String ip, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_user", DaoOperation.Equals, user)
                .where("c_ip", DaoOperation.Equals, ip)
                .where("c_grade", DaoOperation.Less, 99)
                .order("c_last_visit desc")
                .query(OnlineModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<OnlineModel> query(Timestamp visit) {
        return liteOrm.query(new LiteQuery(OnlineModel.class).where("c_last_visit<?"), new Object[]{visit});
    }

    @Override
    public OnlineModel findBySid(String sid) {
        return liteOrm.findOne(new LiteQuery(OnlineModel.class).where("c_sid=?"), new Object[]{sid});
    }

    @Override
    public int count() {
        return liteOrm.count(new LiteQuery(OnlineModel.class), null);
    }

    @Override
    public void save(OnlineModel online) {
        liteOrm.save(online);
    }

    @Override
    public void delete(OnlineModel online) {
        liteOrm.delete(online);
    }

    @Override
    public void deleteById(String id) {
        liteOrm.deleteById(OnlineModel.class, id);
    }

    @Override
    public void deleteByUser(String user) {
        liteOrm.delete(new LiteQuery(OnlineModel.class).where("c_user=?"), new Object[]{user});
    }
}
