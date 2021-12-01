package org.lpw.clivia.chat;

interface ChatDao {
    ChatModel findById(String id);

    void save(ChatModel chat);
}