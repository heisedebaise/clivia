package org.lpw.clivia.sms;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface SmsDao {
    PageList<SmsModel> query(String scene, String pusher, String name, int state, int pageSize, int pageNum);

    SmsModel findById(String id);

    SmsModel find(String scene, int state);

    void save(SmsModel sms);

    void state(String id, int state);

    void time(String id, Timestamp time);

    void delete(String id);
}
