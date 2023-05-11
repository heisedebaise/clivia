package org.lpw.clivia.aliyun;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(AliyunModel.NAME + ".dao")
class AliyunDaoImpl implements AliyunDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<AliyunModel> query(String key, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_key", DaoOperation.Equals, key)
                .query(AliyunModel.class, pageSize, pageNum);
    }

    @Override
    public AliyunModel findById(String id) {
        return liteOrm.findById(AliyunModel.class, id);
    }

    @Override
    public AliyunModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(AliyunModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(AliyunModel aliyun) {
        liteOrm.save(aliyun);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(AliyunModel.class, id);
    }
}