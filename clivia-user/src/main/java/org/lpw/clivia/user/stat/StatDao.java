package org.lpw.clivia.user.stat;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Date;

/**
 * @author lpw
 */
interface StatDao {
    PageList<StatModel> query(String date, int pageSize, int pageNum);

    StatModel find(Date date);

    void save(StatModel stat);
}
