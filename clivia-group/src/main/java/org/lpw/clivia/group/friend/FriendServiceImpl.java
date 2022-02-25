package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.group.GroupService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.scheduler.DateJob;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;

@Service(FriendModel.NAME + ".service")
public class FriendServiceImpl implements FriendService, UserListener, DateJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private GroupService groupService;
    @Inject
    private FriendDao friendDao;

    @Override
    public JSONObject user() {
        return friendDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum())
                .toJson((friend, object) -> object.put("proposer", userService.get(friend.getProposer())));
    }

    @Override
    public void add(String user, String memo) {
        String proposer = userService.id();
        FriendModel friend = friendDao.find(user, proposer);
        if (friend == null) {
            friend = new FriendModel();
            friend.setUser(user);
            friend.setProposer(proposer);
        } else if (friend.getState() != 0)
            return;

        friend.setMemo(memo);
        friend.setTime(dateTime.now());
        friendDao.save(friend);
    }

    @Override
    public void agree(String id) {
        FriendModel friend = state(id, 1);
        if (friend != null)
            groupService.friend(new String[]{friend.getUser(), friend.getProposer()});
    }

    @Override
    public void reject(String id) {
        state(id, 2);
    }

    private FriendModel state(String id, int state) {
        FriendModel friend = friendDao.findById(id);
        if (friend == null || friend.getState() != 0 || !friend.getUser().equals(userService.id()))
            return null;

        friend.setState(state);
        friendDao.save(friend);

        return friend;
    }

    @Override
    public void userSignUp(UserModel user) {
    }

    @Override
    public void userDelete(UserModel user) {
        friendDao.delete(user.getId());
    }

    @Override
    public void executeDateJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        friendDao.state(0, 3, dateTime.getStart(calendar.getTime()));
    }
}
