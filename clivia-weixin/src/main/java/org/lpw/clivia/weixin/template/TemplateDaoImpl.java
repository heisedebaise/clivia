package org.lpw.clivia.weixin.template;

import org.lpw.clivia.util.DaoHelper;
import org.lpw.clivia.util.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Repository(TemplateModel.NAME + ".dao")
class TemplateDaoImpl implements TemplateDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<TemplateModel> query(String key, String weixinKey, int type, String templateId, int state, int pageSize, int pageNum) {
        StringBuilder where = new StringBuilder();
        List<Object> args = new ArrayList<>();
        daoHelper.where(where, args, "c_key", DaoOperation.Equals, key);
        daoHelper.where(where, args, "c_weixin_key", DaoOperation.Equals, weixinKey);
        daoHelper.where(where, args, "c_type", DaoOperation.Equals, type);
        daoHelper.where(where, args, "c_template_id", DaoOperation.Equals, templateId);
        daoHelper.where(where, args, "c_state", DaoOperation.Equals, state);

        return liteOrm.query(new LiteQuery(TemplateModel.class).where(where.toString())
                .order("c_state desc,c_type").size(pageSize).page(pageNum), args.toArray());
    }

    @Override
    public TemplateModel find(String key) {
        return liteOrm.findOne(new LiteQuery(TemplateModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(TemplateModel template) {
        liteOrm.save(template);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(TemplateModel.class, id);
    }
}
