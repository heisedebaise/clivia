package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.group.member.MemberModel;
import org.lpw.clivia.group.member.MemberService;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Pinyin;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(GroupModel.NAME + ".service")
public class GroupServiceImpl implements GroupService, UserListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Pinyin pinyin;
    @Inject
    private Json json;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private MemberService memberService;
    @Inject
    Optional<Set<GroupListener>> listeners;
    @Inject
    private GroupDao groupDao;

    @Override
    public JSONObject get(String id) {
        String user = userService.id();
        GroupModel group = groupDao.findById(id);
        JSONObject object = modelHelper.toJson(group);
        JSONArray members = new JSONArray();
        memberService.list(id).forEach(member -> {
            JSONObject m = member(member);
            if (m == null)
                return;

            if (group.getType() == 0) {
                if (members.isEmpty() || !m.getString("user").equals(user)) {
                    object.put("avatar", m.getString("avatar"));
                    object.put("name", m.getString("nick"));
                }
            }
            members.add(m);
        });
        object.put("members", members);

        return object;
    }

    @Override
    public JSONObject friends() {
        String user = userService.id();
        Map<String, JSONArray> map = new HashMap<>();
        for (GroupModel group : groupDao.query(memberService.groups(user, 0)).getList()) {
            JSONObject friend = friend(user, group);
            if (friend == null)
                continue;

            map.computeIfAbsent(label(friend.getString("nick")), k -> new JSONArray()).add(friend);
        }
        JSONObject object = new JSONObject();
        object.putAll(map);

        return object;
    }

    private JSONObject friend(String user, GroupModel group) {
        List<MemberModel> members = memberService.list(group.getId());
        if (members.isEmpty())
            return null;

        if (members.size() == 1) {
            MemberModel member = members.get(0);

            return member.getUser().equals(user) ? member(member) : null;
        }

        for (MemberModel member : members)
            if (!member.getUser().equals(user))
                return member.getState() == 0 || member.getState() == 1 ? member(member) : null;

        return null;
    }

    private JSONObject member(MemberModel member) {
        UserModel user = userService.findById(member.getUser());
        if (user == null)
            return null;

        JSONObject object = new JSONObject();
        object.put("id", member.getId());
        object.put("group", member.getGroup());
        object.put("user", member.getUser());
        object.put("nick", user.getNick());
        object.put("memo", member.getMemo());
        object.put("avatar", user.getAvatar());
        object.put("state", member.getState());
        object.put("time", dateTime.toString(member.getTime()));

        return object;
    }

    private String label(String string) {
        if (validator.isEmpty(string))
            return "#";

        String str = string.substring(0, 1);
        char ch = string.charAt(0);
        if (ch < 128)
            return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') ? str.toUpperCase() : "#";

        String label = pinyin.get(str).substring(0, 1);

        return label.equals(str) ? "#" : label.toUpperCase();
    }

    @Override
    public JSONObject find(String idUidCode) {
        JSONObject user = userService.find(idUidCode);
        if (!user.containsKey("id"))
            return user;

        user = json.copy(user);
        String uid = userService.id();
        String group = keyvalueService.value(friendsKey(uid, user.getString("id")));
        if (validator.isEmpty(group))
            return user;

        List<MemberModel> members = memberService.list(group);
        if (members.isEmpty())
            return user;

        MemberModel member = null;
        if (members.size() == 1) {
            MemberModel mm = members.get(0);
            if (mm.getUser().equals(uid))
                member = mm;
        } else {
            for (MemberModel mm : members) {
                if (!mm.getUser().equals(uid)) {
                    member = mm;

                    break;
                }
            }
        }
        if (member == null)
            return user;

        user.put("group", group);
        user.put("member", member.getId());
        user.put("memo", member.getMemo());
        user.put("friend", true);

        return user;
    }

    @Override
    public GroupModel friend(String[] users) {
        Set<String> set = new HashSet<>();
        for (String user : users)
            if (!validator.isEmpty(user))
                set.add(user);
        if (set.isEmpty())
            return null;

        GroupModel group = new GroupModel();
        group.setCount(users.length);
        group.setTime(dateTime.now());
        groupDao.save(group);
        memberService.create(group.getId(), set, null);

        Iterator<String> iterator = set.iterator();
        String u1 = iterator.next();
        String u2 = iterator.hasNext() ? iterator.next() : u1;
        keyvalueService.save(friendsKey(u1, u2), group.getId());
        keyvalueService.save(friendsKey(u2, u1), group.getId());

        return group;
    }

    private String friendsKey(String user, String friend) {
        return GroupModel.NAME + ":friends:" + user + ":" + friend;
    }

    @Override
    public void userSignUp(UserModel user) {
        friend(new String[]{user.getId()});
    }

    @Override
    public void userDelete(UserModel user) {
        StringBuilder groups = new StringBuilder();
        StringBuilder users = new StringBuilder();
        memberService.grades(user.getId(), -1).forEach((id, grade) -> {
            GroupModel group = groupDao.findById(id);
            if (group.getType() == 0 || grade == 2) {
                memberService.delete(id);
                groupDao.delete(group);
                groups.append(id);
            } else {
                memberService.delete(id, user.getId());
                users.append(id);
            }
        });

        if (listeners.isEmpty() || listeners.get().isEmpty() || (groups.length() == 0 && users.length() == 0))
            return;

        listeners.get().forEach(listener -> listener.groupDelete(groups.toString(), users.toString(), user.getId()));
    }
}
