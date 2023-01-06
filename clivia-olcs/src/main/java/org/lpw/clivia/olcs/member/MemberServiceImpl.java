package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.TimeUnit;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService, UserListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private MemberDao memberDao;
    private JSONObject object = null;

    @Override
    public JSONObject query() {
        if (object == null) {
            JSONArray all = new JSONArray();
            JSONArray newer = new JSONArray();
            long time = System.currentTimeMillis() - TimeUnit.Day.getTime();
            for (MemberModel member : memberDao.query().getList()) {
                UserModel user = userService.findById(member.getId());
                if (user == null || user.getGrade() > 0)
                    continue;

                JSONObject object = new JSONObject();
                object.put("id", user.getId());
                object.put("nick", user.getNick());
                object.put("avatar", user.getAvatar());
                object.put("content", member.getContent());
                all.add(object);
                if (user.getRegister().getTime() > time)
                    newer.add(object);
            }
            object = new JSONObject();
            object.put("all", all);
            object.put("newer", newer);
        }

        return object;
    }

    @Override
    public void save(String id, String content) {
        MemberModel member = memberDao.findById(id);
        boolean isNull = member == null;
        if (isNull) {
            member = new MemberModel();
            member.setId(id);
        }
        member.setContent(content);
        member.setTime(dateTime.now());
        if (isNull)
            memberDao.insert(member);
        else
            memberDao.save(member);
        object = null;
    }

    @Override
    public void userSync(UserModel user) {
        userSignUp(user);
    }

    @Override
    public void userSignUp(UserModel user) {
        MemberModel member = memberDao.findById(user.getId());
        if (member != null || user.getGrade() > 0)
            return;

        member = new MemberModel();
        member.setId(user.getId());
        member.setTime(user.getRegister());
        memberDao.insert(member);
        object = null;
    }

    @Override
    public void userDelete(UserModel user) {
        memberDao.delete(user.getId());
        object = null;
    }
}
