package org.lpw.clivia.dict;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

@Service(DictModel.NAME + ".service")
public class DictServiceImpl implements DictService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private DictDao dictDao;

    @Override
    public JSONObject query(String key) {
        return dictDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray list(String key) {
        JSONArray array = new JSONArray();
        dictDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).getList().forEach(dict -> {
            JSONObject object = new JSONObject();
            object.put("value", dict.getValue());
            object.put("name", dict.getName());
            array.add(object);
        });

        return array;
    }

    @Override
    public void save(DictModel dict) {
        DictModel model = validator.isId(dict.getId()) ? dictDao.findById(dict.getId()) : null;
        if (model == null)
            dict.setId(null);
        dictDao.save(dict);
    }

    @Override
    public void delete(String id) {
        dictDao.delete(id);
    }
}
