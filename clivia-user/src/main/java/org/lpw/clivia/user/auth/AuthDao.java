package org.lpw.clivia.user.auth;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface AuthDao {
    PageList<AuthModel> query(String user);

    AuthModel findByUid(String uid);

    void save(AuthModel auth);

    void delete(AuthModel auth);

    void delete(String user);
}
