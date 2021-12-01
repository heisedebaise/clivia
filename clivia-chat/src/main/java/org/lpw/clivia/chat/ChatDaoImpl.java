package org.lpw.clivia.chat;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ChatModel.NAME + ".dao")
class ChatDaoImpl implements ChatDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public ChatModel findById(String id) {
        return liteOrm.findById(ChatModel.class, id);
    }

    @Override
    public void save(ChatModel chat) {
        liteOrm.save(chat);
    }
}