package org.lpw.clivia.group.friend;

import org.lpw.photon.dao.orm.PageList;

interface FriendDao {
    PageList<FriendModel> query(String user, int pageSize, int pageNum);

    FriendModel find(String user, String proposer);

    void save(FriendModel friend);
}