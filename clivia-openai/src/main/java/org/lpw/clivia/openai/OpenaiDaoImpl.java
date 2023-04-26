package org.lpw.clivia.openai;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(OpenaiModel.NAME + ".dao")
class OpenaiDaoImpl implements OpenaiDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<OpenaiModel> query(String key, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_key", DaoOperation.Equals, key)
                .query(OpenaiModel.class, pageSize, pageNum);
    }

    @Override
    public OpenaiModel findById(String id) {
        return liteOrm.findById(OpenaiModel.class, id);
    }

    @Override
    public OpenaiModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(OpenaiModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(OpenaiModel openai) {
        liteOrm.save(openai);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(OpenaiModel.class, id);
    }
}