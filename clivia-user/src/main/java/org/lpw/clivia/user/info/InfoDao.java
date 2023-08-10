package org.lpw.clivia.user.info;

import org.lpw.photon.dao.orm.PageList;

import java.util.Set;

interface InfoDao {
    PageList<InfoModel> query(String user);

    InfoModel find(String user, String name);

    Set<String> user(String name, String value);

    int count();

    void save(InfoModel info);

    void delete(String user);

    void close();
}