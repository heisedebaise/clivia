package org.lpw.clivia.user.auth;

import org.lpw.photon.dao.orm.PageList;

interface AuthDao {
    PageList<AuthModel> query(String user);

    PageList<AuthModel> search(String uid);

    AuthModel findByUid(String uid);

    AuthModel find(String user, String type);

    void save(AuthModel auth);

    void delete(AuthModel auth);

    void delete(String user);
}
