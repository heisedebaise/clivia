package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.olcs.OlcsConfig;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.TimeUnit;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Optional;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService, UserListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private Optional<OlcsConfig> config;
    @Inject
    private MemberDao memberDao;
    private JSONObject object = null;
    private int unread = 0;

    @Override
    public JSONObject query() {
        if (object == null) {
            JSONArray all = new JSONArray();
            JSONArray newer = new JSONArray();
            long time = System.currentTimeMillis() - TimeUnit.Day.getTime();
            for (MemberModel member : memberDao.query().getList()) {
                UserModel user = userService.findById(member.getId());
                if (user == null || user.getGrade() > 0) continue;

                JSONObject object = new JSONObject();
                object.put("id", user.getId());
                object.put("nick", user.getNick());
                object.put("name", config.isPresent() ? config.get().getName(user) : user.getNick());
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
        JSONObject unread = new JSONObject();
        memberDao.unread().getList().forEach(member -> unread.put(member.getId(), member.getReplierUnread()));
        object.put("unread", unread);

        return object;
    }

    @Override
    public JSONObject user() {
        JSONObject object = new JSONObject();
        MemberModel member = memberDao.findById(userService.id());
        if (member == null)
            return object;

        object.put("unread", member.getUserUnread());
        object.put("read", dateTime.toString(member.getUserRead()));

        return object;
    }

    @Override
    public JSONObject unread() {
        JSONObject object = new JSONObject();
        int count = memberDao.sum();
        object.put("count", count);
        if (count <= unread)
            object.put("mute", true);
        unread = count;

        return object;
    }

    @Override
    public MemberModel findById(String id) {
        return memberDao.findById(id);
    }

    @Override
    public void save(String id, String content, boolean reply) {
        MemberModel member = memberDao.findById(id);
        boolean isNull = member == null;
        if (isNull) {
            member = new MemberModel();
            member.setId(id);
        }
        member.setContent(content);
        member.setTime(dateTime.now());
        if (reply) member.setUserUnread(member.getUserUnread() + 1);
        else member.setReplierUnread(member.getReplierUnread() + 1);
        if (isNull) memberDao.insert(member);
        else memberDao.save(member);
        object = null;
    }

    @Override
    public void userRead(String id, Timestamp time) {
        memberDao.userRead(id, time);
    }

    @Override
    public void replierRead(String id, Timestamp time) {
        memberDao.replierRead(id, time);
    }

    @Override
    public void clean(String id) {
        MemberModel member = memberDao.findById(id);
        if (member == null) return;

        member.setContent("");
        member.setTime(dateTime.now());
        member.setUserUnread(0);
        member.setReplierUnread(0);
        memberDao.save(member);
    }

    @Override
    public void empty(Timestamp time) {
        memberDao.content("", time);
    }

    @Override
    public void userSync(UserModel user) {
        userSignUp(user);
    }

    @Override
    public void userSignUp(UserModel user) {
        MemberModel member = memberDao.findById(user.getId());
        if (member != null || user.getGrade() > 0) return;

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
