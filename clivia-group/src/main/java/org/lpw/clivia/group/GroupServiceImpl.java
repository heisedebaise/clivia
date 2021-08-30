package org.lpw.clivia.group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.lpw.clivia.group.member.MemberModel;
import org.lpw.clivia.group.member.MemberService;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Pinyin;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

@Service(GroupModel.NAME + ".service")
public class GroupServiceImpl implements GroupService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private Pinyin pinyin;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private MemberService memberService;
    @Inject
    private GroupDao groupDao;

    @Override
    public JSONArray gets() {
        JSONArray array = new JSONArray();
        groupDao.query().getList().forEach(group -> {
            JSONObject object = modelHelper.toJson(group);
            object.put("members", modelHelper.toJson(memberService.list(group.getId())));
            array.add(object);
        });

        return array;
    }

    @Override
    public JSONObject get(String id) {
        return cache.computeIfAbsent(groupCacheKey(id), key -> {
            String user = userService.id();
            GroupModel group = groupDao.findById(id);
            JSONObject object = modelHelper.toJson(group);
            JSONArray members = new JSONArray();
            memberService.list(id).forEach(member -> {
                JSONObject m = member(group, member);
                if (m == null)
                    return;

                if (group.getType() == 0) {
                    if (members.isEmpty() || !m.getString("user").equals(user))
                        object.put("name", m.getString("nick"));
                }
                members.add(m);
            });

            object.put("members", members);

            return object;
        }, false);
    }

    @Override
    public JSONObject friends() {
        String user = userService.id();
        return cache.computeIfAbsent(friendsCacheKey(user), key -> {
            Map<String, JSONArray> map = new HashMap<>();
            boolean self = false;
            for (GroupModel group : groupDao.query(memberService.user(0)).getList()) {
                JSONObject friend = friend(user, group);
                if (friend == null)
                    continue;

                map.computeIfAbsent(label(friend.getString("nick")), k -> new JSONArray()).add(friend);
                if (!self && friend.getString("user").equals(user))
                    self = true;
            }

            if (!self) {
                JSONObject friend = friend(user, friend(new String[] { user }));
                if (friend != null)
                    map.computeIfAbsent(label(friend.getString("nick")), k -> new JSONArray()).add(friend);
            }

            JSONObject object = new JSONObject();
            map.forEach(object::put);

            return object;
        }, false);
    }

    private JSONObject friend(String user, GroupModel group) {
        List<MemberModel> members = memberService.list(group.getId());
        if (members.isEmpty())
            return null;

        if (members.size() == 1) {
            MemberModel member = members.get(0);

            return member.getUser().equals(user) ? member(group, member) : null;
        }

        for (MemberModel member : members)
            if (!member.getUser().equals(user))
                return member.getState() == 0 || member.getState() == 1 ? member(group, member) : null;

        return null;
    }

    private JSONObject member(GroupModel group, MemberModel member) {
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
        String u2 = u1;
        if (iterator.hasNext())
            u2 = iterator.next();
        keyvalueService.save(friendsKey(u1, u2), group.getId());
        keyvalueService.save(friendsKey(u2, u1), group.getId());
        set.forEach(this::cleanFriendsCache);

        return group;
    }

    private String friendsKey(String user, String friend) {
        return GroupModel.NAME + ":friends:" + user + ":" + friend;
    }

    private String groupCacheKey(String id) {
        return GroupModel.NAME + ":" + id;
    }

    @Override
    public void cleanFriendsCache(String user) {
        cache.remove(friendsCacheKey(user));
    }

    private String friendsCacheKey(String user) {
        return GroupModel.NAME + ":friends:" + user;
    }
}
