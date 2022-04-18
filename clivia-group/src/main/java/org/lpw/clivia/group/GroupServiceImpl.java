package org.lpw.clivia.group;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.group.friend.FriendService;
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
    private FriendService friendService;
    @Inject
    private MemberService memberService;
    @Inject
    private Optional<Set<GroupListener>> listeners;
    @Inject
    private GroupDao groupDao;

    @Override
    public JSONObject get(String id) {
        GroupModel group = groupDao.findById(id);
        JSONObject object = modelHelper.toJson(group);
        members(object, id, group, userService.id());

        return object;
    }

    @Override
    public JSONObject members(String id) {
        JSONObject object = new JSONObject();
        members(object, id, null, "");

        return object;
    }

    private void members(JSONObject object, String id, GroupModel group, String user) {
        JSONArray members = new JSONArray();
        memberService.list(id).forEach(member -> {
            if (group != null && group.getType() == 1 && member.getUser().equals(user)) {
                if (member.getGrade() == 2)
                    object.put("owner", true);
                else if (member.getGrade() == 1)
                    object.put("master", true);
            }

            JSONObject m = member(member);
            if (m == null)
                return;

            if (group != null && group.getType() == 0) {
                if (members.isEmpty() || !m.getString("user").equals(user)) {
                    object.put("avatar", m.getString("avatar"));
                    object.put("name", m.getString("nick"));
                }
            }
            members.add(m);
        });
        object.put("members", members);
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
        memberService.create(group.getId(), set, 0, null);

        Iterator<String> iterator = set.iterator();
        String u1 = iterator.next();
        String u2 = iterator.hasNext() ? iterator.next() : u1;
        keyvalueService.save(friendsKey(u1, u2), group.getId());
        keyvalueService.save(friendsKey(u2, u1), group.getId());
        if (set.size() == 2)
            listeners.ifPresent(s -> s.forEach(listener -> listener.groupCreate(group, null)));

        return group;
    }

    @Override
    public String self(String user) {
        String id = keyvalueService.value(friendsKey(user, user));
        if (validator.isId(id))
            return id;

        return friend(new String[]{user}).getId();
    }

    @Override
    public int start(String name, String avatar, String prologue, String[] users) {
        Set<String> set = new HashSet<>(Arrays.asList(users));
        if (set.size() < 3) return 1;

        String owner = userService.id();
        if (!set.contains(owner)) return 2;

        if (validator.isEmpty(name)) {
            StringBuilder sb = new StringBuilder();
            for (String id : users) {
                UserModel user = userService.findById(id);
                if (user != null && !validator.isEmpty(user.getNick()))
                    sb.append(',').append(user.getNick());
            }
            name = sb.length() == 0 ? "" : sb.substring(1);
        }

        GroupModel group = new GroupModel();
        group.setType(1);
        group.setName(name);
        group.setAvatar(avatar);
        group.setCount(set.size());
        group.setTime(dateTime.now());
        groupDao.save(group);
        memberService.create(group.getId(), set, 1, owner);
        listeners.ifPresent(gls -> gls.forEach(listener -> listener.groupCreate(group, prologue)));

        return 0;
    }

    @Override
    public void delete(String id) {
        GroupModel group = groupDao.findById(id);
        if (group == null)
            return;

        MemberModel member = memberService.find(id, userService.id());
        if (member == null)
            return;

        delete(group, member.getUser(), member.getGrade());
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
        memberService.grades(user.getId(), -1).forEach((id, grade) -> {
            GroupModel group = groupDao.findById(id);
            if (group != null)
                delete(group, user.getId(), grade);
        });
    }

    private void delete(GroupModel group, String user, int grade) {
        if (group.getType() == 0 || grade == 2) {
            groupDao.delete(group);
            List<MemberModel> members = memberService.list(group.getId());
            memberService.delete(group.getId());
            if (group.getType() == 0 && !members.isEmpty()) {
                String user1 = members.get(0).getUser();
                String user2 = members.size() > 1 ? members.get(1).getUser() : user;
                keyvalueService.delete(friendsKey(user1, user2));
                if (!user1.equals(user2)) {
                    keyvalueService.delete(friendsKey(user2, user1));
                    friendService.delete(user1, user2);
                }
            }
            listeners.ifPresent(set -> set.forEach(listener -> listener.groupDelete(group, members)));
        } else {
            memberService.delete(group.getId(), user);
            listeners.ifPresent(set -> set.forEach(listener -> listener.groupUpdate(group)));
        }
    }
}
