package org.lpw.clivia.group;

import org.lpw.photon.dao.orm.PageList;

interface GroupDao {
    PageList<GroupModel> query(int pageSize, int pageNum);
}