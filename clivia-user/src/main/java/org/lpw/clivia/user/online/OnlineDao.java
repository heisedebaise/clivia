package org.lpw.clivia.user.online;

import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface OnlineDao {
    PageList<OnlineModel> query(String user, String ip, int pageSize, int pageNum);

    PageList<OnlineModel> query(Timestamp visit);

    OnlineModel findBySid(String sid);

    SqlTable user(Timestamp[] lastVisit);

    void save(OnlineModel online);

    void delete(OnlineModel online);

    void deleteById(String id);

    void deleteByUser(String user);
}
