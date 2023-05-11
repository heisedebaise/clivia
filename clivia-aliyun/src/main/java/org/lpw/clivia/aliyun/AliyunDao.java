package org.lpw.clivia.aliyun;

import org.lpw.photon.dao.orm.PageList;

interface AliyunDao {
    PageList<AliyunModel> query(String key, int pageSize, int pageNum);

    AliyunModel findById(String id);

    AliyunModel findByKey(String key);

    void save(AliyunModel aliyun);

    void delete(String id);
}