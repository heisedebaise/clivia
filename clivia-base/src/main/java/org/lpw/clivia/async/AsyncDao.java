package org.lpw.clivia.async;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface AsyncDao {
    PageList<AsyncModel> query(int state, Timestamp timeout);

    AsyncModel findById(String id);

    void save(AsyncModel async);
}
