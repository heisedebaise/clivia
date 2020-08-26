package org.lpw.clivia.upgrader;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface UpgraderDao {
    PageList<UpgraderModel> query(int pageSize, int pageNum);

    UpgraderModel findById(String id);

    UpgraderModel latest(int version, int client);

    void save(UpgraderModel upgrader);

    void delete(String id);
}
