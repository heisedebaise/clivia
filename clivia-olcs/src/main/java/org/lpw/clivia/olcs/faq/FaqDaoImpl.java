package org.lpw.clivia.olcs.faq;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(FaqModel.NAME + ".dao")
class FaqDaoImpl implements FaqDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<FaqModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(FaqModel.class).order("c_sort").size(pageSize).page(pageNum), null);
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