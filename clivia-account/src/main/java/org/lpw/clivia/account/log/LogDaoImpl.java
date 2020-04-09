package org.lpw.clivia.account.log;

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
@Repository(LogModel.NAME + ".dao")
class LogDaoImpl implements LogDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<LogModel> query(String user, String owner, String type, String channel, int state, Timestamp start,
                                    Timestamp end, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_user", DaoOperation.Equals, user)
                .where("c_owner", DaoOperation.Equals, owner)
                .where("c_type", DaoOperation.Equals, type)
                .where("c_channel", DaoOperation.Equals, channel)
                .where("c_state", DaoOperation.Equals, state)
                .where("c_start", DaoOperation.GreaterEquals, start)
                .where("c_start", DaoOperation.LessEquals, end)
                .order("c_start desc,c_index desc")
                .query(LogModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<LogModel> query(int restate) {
        return liteOrm.query(new LiteQuery(LogModel.class).where("c_restate=?"), new Object[]{restate});
    }

    @Override
    public LogModel findById(String id) {
        return liteOrm.findById(LogModel.class, id);
    }

    @Override
    public void save(LogModel log) {
        liteOrm.save(log);
        liteOrm.close();
    }
}
