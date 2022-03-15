package org.lpw.clivia.group.member;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService {
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private MemberDao memberDao;

    @Override
    public Set<String> groups(String user, int type) {
        Set<String> set = new HashSet<>();
        memberDao.query(user, type).getList().forEach(member -> set.add(member.getGroup()));

        return set;
    }

    @Override
    public Map<String, Integer> grades(String user, int type) {
        Map<String, Integer> map = new HashMap<>();
        memberDao.query(user, type).getList().forEach(member -> map.put(member.getGroup(), member.getGrade()));

        return map;
    }

    @Override
    public List<MemberModel> list(String group) {
        return memberDao.query(group).getList();
    }

    @Override
    public MemberModel find(String group, String user) {
        return memberDao.find(group, user);
    }

    @Override
    public String groups() {
        StringBuilder sb = new StringBuilder();
        memberDao.query(userService.id(), -1).getList().forEach(member -> sb.append(',').append(member.getGroup()));

        return sb.length() == 0 ? "" : sb.substring(1);
    }

    @Override
    public void create(String group, Set<String> users, String owner) {
        Timestamp now = dateTime.now();
        for (String user : users) {
            if (memberDao.find(group, user) != null)
                continue;

            MemberModel member = new MemberModel();
            member.setGroup(group);
            member.setUser(user);
            if (user.equals(owner))
                member.setGrade(2);
            member.setTime(now);
            memberDao.save(member);
        }
    }

    @Override
    public void memo(String id, String memo) {
        MemberModel member = memberDao.findById(id);
        member.setMemo(memo);
        memberDao.save(member);
    }

    @Override
    public void delete(String group) {
        memberDao.delete(group);
    }

    @Override
    public void delete(String group, String user) {
        memberDao.delete(group, user);
    }
}
