package org.lpw.clivia.olcs;

import org.lpw.photon.dao.orm.PageList;

interface OlcsDao {
    PageList<OlcsModel> query(String user, int pageSize, int pageNum);

    OlcsModel findById(String id);

    void save(OlcsModel olcs);
}