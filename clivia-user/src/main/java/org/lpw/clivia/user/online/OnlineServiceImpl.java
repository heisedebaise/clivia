package org.lpw.clivia.user.online;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Service(OnlineModel.NAME + ".service")
public class OnlineServiceImpl implements OnlineService, MinuteJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private OnlineDao onlineDao;
    @Value("${" + OnlineModel.NAME + ".effective:30}")
    private int effective;

    @Override
    public JSONObject query(String user, String uid, String ip) {
        return onlineDao.query(getUser(user, uid), ip, pagination.getPageSize(20), pagination.getPageNum())
                .toJson((online, object) -> object.put("user", userService.get(online.getUser())));
    }

    @Override
    public OnlineModel findBySid(String sid) {
        return onlineDao.findBySid(sid);
    }

    @Override
    public void signIn(UserModel user) {
        String sid = session.getId();
        OnlineModel online = onlineDao.findBySid(sid);
        if (online == null) {
            online = new OnlineModel();
            online.setSid(sid);
        }
        online.setUser(user.getId());
        online.setGrade(user.getGrade());
        online.setIp(header.getIp());
        online.setSignIn(dateTime.now());
        online.setLastVisit(dateTime.now());
        onlineDao.save(online);
    }

    @Override
    public boolean isSign() {
        OnlineModel online = onlineDao.findBySid(session.getId());
        if (online == null)
            return false;

        if (System.currentTimeMillis() - online.getLastVisit().getTime() > TimeUnit.Minute.getTime(1)) {
            online.setLastVisit(dateTime.now());
            onlineDao.save(online);
        }

        return true;
    }

    @Override
    public void signOut() {
        OnlineModel online = onlineDao.findBySid(session.getId());
        if (online != null)
            onlineDao.delete(online);
    }

    @Override
    public void signOutId(String id) {
        onlineDao.deleteById(id);
    }

    @Override
    public void signOutUser(String user) {
        onlineDao.deleteByUser(user);
    }

    private String getUser(String user, String uid) {
        if (!validator.isEmpty(user) || validator.isEmpty(uid))
            return user;

        AuthModel auth = authService.findByUid(uid);

        return auth == null ? uid : auth.getUser();
    }

    @Override
    public int count(Date date) {
        Set<String> set = new HashSet<>();
        onlineDao.user(dateTime.toTimeRange(date)).forEach(list -> set.add((String) list.get(0)));

        return set.size();
    }

    @Override
    public void executeMinuteJob() {
        onlineDao.query(new Timestamp(System.currentTimeMillis() - TimeUnit.Minute.getTime(effective))).getList().forEach(online -> {
            userService.signOut(online.getSid());
            onlineDao.delete(online);
        });
    }
}
