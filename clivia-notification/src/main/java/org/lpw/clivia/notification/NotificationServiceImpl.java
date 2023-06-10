package org.lpw.clivia.notification;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Service(NotificationModel.NAME + ".service")
public class NotificationServiceImpl implements NotificationService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Thread thread;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private NotificationDao notificationDao;

    @Override
    public JSONObject user(String genre) {
        return notificationDao.query(userService.id(), genre, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject unread(String[] genre, boolean waitable) {
        String user = userService.id();
        if (validator.isEmpty(user)) {
            thread.sleep(10, TimeUnit.Second);

            return new JSONObject();
        }

        Set<String> set = new HashSet<>();
        for (String g : genre)
            if (!validator.isEmpty(g))
                set.add(g);
        Timestamp expiration = dateTime.now();
        if (waitable) {
            for (int i = 0; i < 10; i++) {
                NotificationModel notification = notificationDao.unread(user, set, expiration);
                if (notification == null) {
                    notificationDao.close();
                    thread.sleep(1, TimeUnit.Second);

                    continue;
                }

                return read(notification);
            }

            return new JSONObject();
        } else {
            NotificationModel notification = notificationDao.unread(user, set, expiration);

            return notification == null ? new JSONObject() : read(notification);
        }
    }

    JSONObject read(NotificationModel notification) {
        notification.setRead(dateTime.now());
        notificationDao.save(notification);

        return modelHelper.toJson(notification);
    }

    @Override
    public void save(String user, String genre, String subject, String content, Timestamp expiration) {
        NotificationModel notification = new NotificationModel();
        notification.setUser(user);
        notification.setGenre(genre);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setExpiration(expiration);
        notification.setTime(dateTime.now());
        notificationDao.save(notification);
    }
}
