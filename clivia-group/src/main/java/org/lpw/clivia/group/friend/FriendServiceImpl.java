package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.scheduler.DateJob;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import javax.inject.Inject;

@Service(FriendModel.NAME + ".service")
public class FriendServiceImpl implements FriendService, DateJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
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
    public void executeDateJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        friendDao.state(0, 3, dateTime.getStart(calendar.getTime()));
    }

    @Override
    public void agree(String id) {
        state(id, 1);
    }

    @Override
    public void reject(String id) {
        state(id, 2);
    }

    private void state(String id, int state) {
        FriendModel friend = friendDao.findById(id);
        if (friend == null || friend.getState() != 0 || !friend.getUser().equals(userService.id()))
            return;

        friend.setState(state);
        friendDao.save(friend);
    }
}
