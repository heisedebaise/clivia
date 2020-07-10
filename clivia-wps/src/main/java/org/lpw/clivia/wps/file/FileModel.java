package org.lpw.clivia.wps.file;

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
@Component(FileModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = FileModel.NAME)
@Table(name = "t_wps_file")
public class FileModel extends ModelSupport {
    static final String NAME = "clivia.wps.file";

    private String wps; // WPS
    private String uri; // URI
    private String name; // 文件名
    private int permission; // 权限：0-只读；1-读写
    private int version; // 版本号
    private long size; // 文件大小
    private String creator; // 创建者
    private long create; // 创建时间
    private String modifier; // 修改者
    private long modify; // 修改时间

    @Jsonable
    @Column(name = "c_wps")
    public String getWps() {
        return wps;
    }

    public void setWps(String wps) {
        this.wps = wps;
    }

    @Jsonable
    @Column(name = "c_uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
    @Column(name = "c_permission")
    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    @Jsonable
    @Column(name = "c_version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Jsonable
    @Column(name = "c_size")
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Jsonable
    @Column(name = "c_creator")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Jsonable
    @Column(name = "c_create")
    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    @Jsonable
    @Column(name = "c_modifier")
    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Jsonable
    @Column(name = "c_modify")
    public long getModify() {
        return modify;
    }

    public void setModify(long modify) {
        this.modify = modify;
    }
}
