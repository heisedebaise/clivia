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
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private MemberService memberService;
    @Inject
    private GroupDao groupDao;

    @Override
    public JSONObject friends() {
        String user = userService.id();
        return cache.computeIfAbsent(friendsCacheKey(user), key -> {
            Map<String, JSONArray> map = new HashMap<>();
            for (GroupModel group : groupDao.query(memberService.user(0)).getList()) {
                JSONObject friend = friend(user, group);
                if (friend == null)
                    continue;

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

            return member.getUser().equals(user) ? friend(group, member) : null;
        }

        for (MemberModel member : members)
            if (!member.getUser().equals(user))
                return member.getState() == 0 || member.getState() == 1 ? friend(group, member) : null;

        return null;
    }

    private JSONObject friend(GroupModel group, MemberModel member) {
        UserModel user = userService.findById(member.getUser());
        if (user == null)
            return null;

        JSONObject object = new JSONObject();
        object.put("id", group.getId());
        object.put("user", member.getUser());
        object.put("nick", validator.isEmpty(member.getMemo()) ? user.getNick() : member.getMemo());
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
        if (user.containsKey("id")
                && keyvalueService.exists(friendsKey(userService.id(), user.getString("id")), "true"))
            user.put("friend", true);

        return user;
    }

    @Override
    public void friend(String[] users) {
        Set<String> set = new HashSet<>();
        for (String user : users)
            if (!validator.isEmpty(user))
                set.add(user);
        if (set.isEmpty())
            return;

        GroupModel group = new GroupModel();
        group.setCount(users.length);
        group.setTime(dateTime.now());
        groupDao.save(group);
        memberService.create(group.getId(), set, null);

        if (set.size() == 2) {
            Iterator<String> iterator = set.iterator();
            String u1 = iterator.next();
            String u2 = iterator.next();
            keyvalueService.save(friendsKey(u1, u2), "true");
            keyvalueService.save(friendsKey(u2, u1), "true");
        }
        set.forEach(this::cleanFriendsCache);
    }

    private String friendsKey(String user, String friend) {
        return GroupModel.NAME + ":friends:" + user + ":" + friend;
    }

    private void cleanFriendsCache(String user) {
        cache.remove(friendsCacheKey(user));
    }

    private String friendsCacheKey(String user) {
        return GroupModel.NAME + ":friends:" + user;
    }
}
