package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(MemberModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = MemberModel.NAME)
@Table(name = "t_olcs_member")
public class MemberModel extends ModelSupport {
    static final String NAME = "clivia.olcs.member";

    private String content; // 内容
    private Timestamp time; // 消息时间


    @Jsonable
    @Column(name = "c_content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Jsonable
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}