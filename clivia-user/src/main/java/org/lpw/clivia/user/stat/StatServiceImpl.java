package org.lpw.clivia.user.stat;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.TimeUnit;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.util.Calendar;

@Service(StatModel.NAME + ".service")
public class StatServiceImpl implements StatService, MinuteJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private StatDao statDao;

    @Override
    public JSONObject query(String date) {
        return statDao.query(date, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject today() {
        StatModel stat = statDao.find(dateTime.today());
        if (stat == null)
            stat = new StatModel();

        return modelHelper.toJson(stat);
    }

    @Override
    public void executeMinuteJob() {
        Calendar calendar = Calendar.getInstance();
        count(new Date(calendar.getTimeInMillis()));
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            count(new Date(calendar.getTimeInMillis()));
        }
    }

    private void count(Date date) {
        StatModel stat = statDao.find(date);
        if (stat == null) {
            stat = new StatModel();
            stat.setDate(date);
        }
        stat.setCount(userService.count());
        stat.setRegister(userService.count(date));
        stat.setOnline(Math.max(stat.getOnline(), onlineService.count(date)));
        statDao.save(stat);
    }
}
