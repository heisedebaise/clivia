package org.lpw.clivia.user.inviter;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface InviterDao {
    InviterModel findByPsid(String psid);

    void save(InviterModel inviter);

    void clean(Timestamp time);
}
