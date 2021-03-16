package org.lpw.clivia.module;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ModuleModel.NAME + ".service")
public class ModuleServiceImpl implements ModuleService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private ModuleDao moduleDao;

    @Override
    public JSONObject query() {
        return moduleDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(ModuleModel module) {
        ModuleModel model = validator.isId(module.getId()) ? moduleDao.findById(module.getId()) : null;
        if (model == null)
            module.setId(null);
        moduleDao.save(module);
    }

    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }
}
