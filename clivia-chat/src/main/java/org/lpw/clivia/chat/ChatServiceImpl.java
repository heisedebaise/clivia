package org.lpw.clivia.chat;

import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ChatModel.NAME + ".service")
public class ChatServiceImpl implements ChatService {
    @Inject
    private Validator validator;
    @Inject
    private ChatDao chatDao;

    @Override
    public void save(ChatModel chat) {
        ChatModel model = validator.isId(chat.getId()) ? chatDao.findById(chat.getId()) : null;
        if (model == null)
            chat.setId(null);
        chatDao.save(chat);
    }
}
