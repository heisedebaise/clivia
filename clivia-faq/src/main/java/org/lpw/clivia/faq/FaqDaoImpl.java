package org.lpw.clivia.faq;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(FaqModel.NAME + ".dao")
class FaqDaoImpl implements FaqDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<FaqModel> query(String key, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_key", DaoOperation.Equals, key)
                .order("c_sort")
                .query(FaqModel.class, pageSize, pageNum);
    }

    @Override
    public FaqModel findById(String id) {
        return liteOrm.findById(FaqModel.class, id);
    }

    @Override
    public void save(FaqModel faq) {
        liteOrm.save(faq);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(FaqModel.class, id);
    }
}