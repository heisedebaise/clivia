package org.lpw.clivia.aliyun;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(AliyunModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = AliyunModel.NAME)
@Table(name = "t_aliyun")
public class AliyunModel extends ModelSupport {
    static final String NAME = "clivia.aliyun";

    private String key; // 引用KEY
    private String name; // 名称
    private String accessKeyId; // AccessKey ID
    private String accessKeySecret; // AccessKey Secret
    private String regionId; // 实例区域ID
    private String endpoint; // 实例区域域名
    private String instanceName; // 实例名称

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
    @Column(name = "c_access_key_id")
    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Jsonable
    @Column(name = "c_access_key_secret")
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    @Jsonable
    @Column(name = "c_region_id")
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Jsonable
    @Column(name = "c_endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Jsonable
    @Column(name = "c_instance_name")
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}