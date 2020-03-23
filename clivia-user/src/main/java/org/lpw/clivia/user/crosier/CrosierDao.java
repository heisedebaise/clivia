package org.lpw.clivia.user.crosier;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface CrosierDao {
    PageList<CrosierModel> query(int grade);

    void save(CrosierModel crosier);

    void delete(int grade);
}
