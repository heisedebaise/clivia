package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(FriendModel.NAME + ".service")
public class FriendServiceImpl implements FriendService {
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
        return friendDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum()).toJson();
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
}
