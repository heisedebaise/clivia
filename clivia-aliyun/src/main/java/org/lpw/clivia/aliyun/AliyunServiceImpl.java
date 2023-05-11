package org.lpw.clivia.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(AliyunModel.NAME + ".service")
public class AliyunServiceImpl implements AliyunService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
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
