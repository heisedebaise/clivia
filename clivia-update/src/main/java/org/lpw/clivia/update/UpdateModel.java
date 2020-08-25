package org.lpw.clivia.update;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lpw
 */
@Component(UpdateModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = UpdateModel.NAME)
@Table(name = "t_update")
public class UpdateModel extends ModelSupport {
    static final String NAME = "clivia.update";

    private int version; // 版本号
    private int forced; // 强制升级：0-否；1-是
    private String client; // 客户端：0-Android；1-iOS
    private String explain; // 说明
    private String url; // 升级地址

    @Jsonable
    @Column(name = "c_version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Jsonable
    @Column(name = "c_forced")
    public int getForced() {
        return forced;
    }

    public void setForced(int forced) {
        this.forced = forced;
    }

    @Jsonable
    @Column(name = "c_client")
    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Jsonable
    @Column(name = "c_explain")
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Jsonable
    @Column(name = "c_url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}