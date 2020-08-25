package org.lpw.clivia.update;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service(UpdateModel.NAME + ".service")
public class UpdateServiceImpl implements UpdateService {
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UpdateDao updateDao;

    @Override
    public JSONObject query() {
        return updateDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(int version, int client) {
        UpdateModel update = updateDao.latest(version, client);

        return update == null ? new JSONObject() : modelHelper.toJson(update);
    }

    @Override
    public void save(UpdateModel update) {
        if (validator.isEmpty(update.getId()) || updateDao.findById(update.getId()) == null)
            update.setId(null);
        updateDao.save(update);
    }

    @Override
    public void delete(String id) {
        updateDao.delete(id);
    }
}
