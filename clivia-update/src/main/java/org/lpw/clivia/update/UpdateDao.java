package org.lpw.clivia.update;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface UpdateDao {
    PageList<UpdateModel> query(int pageSize, int pageNum);

    UpdateModel findById(String id);

    UpdateModel latest(int version, int client);

    void save(UpdateModel update);

    void delete(String id);
}
