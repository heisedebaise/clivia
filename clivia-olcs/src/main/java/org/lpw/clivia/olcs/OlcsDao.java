package org.lpw.clivia.olcs;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface OlcsDao {
    PageList<OlcsModel> query(String user, Timestamp time);

    OlcsModel findById(String id);

    void save(OlcsModel olcs);

    void read(String user, boolean replier, int read);

    void delete(Timestamp time);
}