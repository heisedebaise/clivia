package org.lpw.clivia.pair;

import org.lpw.photon.dao.orm.PageList;

interface PairDao {
    PageList<PairModel> query(String owner);

    int count(String owner, String value);

    int count(String owner);

    void save(PairModel pair);

    void delete(String owner, String value);
}