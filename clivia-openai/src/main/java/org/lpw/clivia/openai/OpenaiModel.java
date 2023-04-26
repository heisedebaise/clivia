package org.lpw.clivia.openai;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(OpenaiModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = OpenaiModel.NAME)
@Table(name = "t_openai")
public class OpenaiModel extends ModelSupport {
    static final String NAME = "clivia.openai";

    private String key; // KEY
    private String authorization; // 认证KEY
    private String organization; // 组织ID
    private String chat; // 聊天模型

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_authorization")
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Jsonable
    @Column(name = "c_organization")
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Jsonable
    @Column(name = "c_chat")
    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }
}