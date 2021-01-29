package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(UpgraderModel.NAME + ".service")
public class UpgraderServiceImpl implements UpgraderService {
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UpgraderDao upgraderDao;

    @Override
    public JSONObject query() {
        return upgraderDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(int version, int client) {
        UpgraderModel upgrader = upgraderDao.latest(version, client);

        return upgrader == null ? new JSONObject() : modelHelper.toJson(upgrader);
    }

    @Override
    public void save(UpgraderModel upgrader) {
        if (validator.isEmpty(upgrader.getId()) || upgraderDao.findById(upgrader.getId()) == null)
            upgrader.setId(null);
        upgraderDao.save(upgrader);
    }

    @Override
    public void delete(String id) {
        upgraderDao.delete(id);
    }
}
