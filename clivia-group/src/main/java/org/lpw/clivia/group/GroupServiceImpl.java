package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(GroupModel.NAME + ".service")
public class GroupServiceImpl implements GroupService {
    @Inject
    private Pagination pagination;
    @Inject
    private GroupDao groupDao;

    @Override
    public JSONObject query() {
        return groupDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject user() {
        return groupDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }
}
