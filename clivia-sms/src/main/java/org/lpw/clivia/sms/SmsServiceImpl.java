package org.lpw.clivia.sms;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Service(SmsModel.NAME + ".service")
public class SmsServiceImpl implements SmsService, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private SmsDao smsDao;
    private final Map<String, SmsPusher> pushers = new HashMap<>();

    @Override
    public JSONObject query(String scene, String pusher, String name, int state) {
        return smsDao.query(scene, pusher, name, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(SmsModel sms) {
        SmsModel model = validator.isEmpty(sms.getId()) ? null : smsDao.findById(sms.getId());
        if (model == null) {
            sms.setId(null);
            sms.setTime(dateTime.now());
        } else
            sms.setTime(model.getTime());
        smsDao.save(sms);
    }

    @Override
    public void state(String id, int state) {
        smsDao.state(id, state);
    }

    @Override
    public void delete(String id) {
        smsDao.delete(id);
    }

    @Override
    public int getContextRefreshedSort() {
        return 108;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(SmsPusher.class).forEach(pusher -> pushers.put(pusher.key(), pusher));
    }
}
