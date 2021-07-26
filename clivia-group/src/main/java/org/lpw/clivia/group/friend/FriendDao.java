package org.lpw.clivia.group.friend;

import java.sql.Timestamp;

import org.lpw.photon.dao.orm.PageList;

interface FriendDao {
    PageList<FriendModel> query(String user, int pageSize, int pageNum);

    FriendModel find(String user, String proposer);

    void save(FriendModel friend);

    void state(int oldState,int newState,Timestamp time);
}