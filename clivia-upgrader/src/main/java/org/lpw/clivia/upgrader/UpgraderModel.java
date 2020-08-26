package org.lpw.clivia.upgrader;

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
@Component(UpgraderModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = UpgraderModel.NAME)
@Table(name = "t_upgrader")
public class UpgraderModel extends ModelSupport {
    static final String NAME = "clivia.upgrader";

    private int version; // 版本号
    private String name; // 版本名
    private int forced; // 强制升级：0-否；1-是
    private int client; // 客户端：0-Android；1-iOS
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
    @Column(name = "c_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public int getClient() {
        return client;
    }

    public void setClient(int client) {
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
