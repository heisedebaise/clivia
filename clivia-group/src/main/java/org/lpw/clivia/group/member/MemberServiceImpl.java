package org.lpw.clivia.group.member;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.lpw.clivia.group.GroupService;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService {
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private GroupService groupService;
    @Inject
    private MemberDao memberDao;

    @Override
    public Set<String> user(int type) {
        Set<String> groups = new HashSet<>();
        memberDao.query(userService.id(), type).getList().forEach(member -> groups.add(member.getGroup()));

        return groups;
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
        groupService.cleanFriendsCache(userService.id());
    }
}
