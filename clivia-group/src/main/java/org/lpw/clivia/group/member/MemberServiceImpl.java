package org.lpw.clivia.group.member;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private MemberDao memberDao;

    @Override
    public JSONObject user() {
        return memberDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(String group, String[] users) {
        Set<String> set = new HashSet<>();
        for (String user : users)
            if (!validator.isEmpty(user))
                set.add(user);
        if (set.isEmpty())
            return;
    }
}
