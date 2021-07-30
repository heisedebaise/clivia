package org.lpw.clivia.group;

import java.util.Set;

import org.lpw.photon.dao.orm.PageList;

interface GroupDao {
    PageList<GroupModel> query(Set<String> ids);

    void save(GroupModel group);
}