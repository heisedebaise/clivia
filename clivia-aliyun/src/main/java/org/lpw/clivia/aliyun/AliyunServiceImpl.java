package org.lpw.clivia.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

@Service(AliyunModel.NAME + ".service")
public class AliyunServiceImpl implements AliyunService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private Optional<Set<AliyunListener>> listeners;
    @Inject
    private AliyunDao aliyunDao;

    @Override
    public JSONObject query(String key) {
        return aliyunDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(AliyunModel aliyun) {
        AliyunModel model = validator.isId(aliyun.getId()) ? aliyunDao.findById(aliyun.getId()) : null;
        if (model == null)
            aliyun.setId(null);
        aliyunDao.save(aliyun);
    }

    @Override
    public void sync(String id) {
        AliyunModel aliyun = aliyunDao.findById(id);
        if (aliyun == null)
            return;

        listeners.ifPresent(set -> set.forEach(listener -> listener.sync(aliyun.getKey())));
    }

    @Override
    public void delete(String id) {
        aliyunDao.delete(id);
    }

    @Override
    public Config config(AliyunModel aliyun) {
        Config config = new Config();
        config.accessKeyId = aliyun.getAccessKeyId();
        config.accessKeySecret = aliyun.getAccessKeySecret();
        config.setType("access_key");
        config.regionId = aliyun.getRegionId();
        config.endpoint = aliyun.getEndpoint();

        return config;
    }
}
