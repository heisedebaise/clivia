package org.lpw.clivia.wps;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.wps.file.FileService;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service(WpsModel.NAME + ".service")
public class WpsServiceImpl implements WpsService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private FileService fileService;
    @Inject
    private WpsDao wpsDao;

    @Override
    public JSONObject query(String key, String name, String appId) {
        return wpsDao.query(key, name, appId, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(WpsModel wps) {
        if (validator.isEmpty(wps.getId()) || wpsDao.findById(wps.getId()) == null)
            wps.setId(null);
        wpsDao.save(wps);
    }

    @Override
    public void delete(String id) {
        wpsDao.delete(id);
        fileService.delete(id);
    }

    @Override
    public String preview(String key, String uri, String name, Permission permission, String creator, long create) {
        return fileService.preview(wpsDao.findByKey(key), uri, name, permission.ordinal(), creator, create);
    }
}
