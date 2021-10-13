package org.lpw.clivia.sms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(SmsModel.NAME + ".service")
public class SmsServiceImpl implements SmsService, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Message message;
    @Inject
    private Generator generator;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Cache cache;
    @Inject
    private Pagination pagination;
    @Inject
    private Header header;
    @Inject
    private Templates templates;
    @Inject
    private Session session;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private SmsDao smsDao;
    private final Map<String, SmsPusher> pushers = new HashMap<>();
    private final JSONArray pushersArray = new JSONArray();

    @Override
    public JSONObject query(String scene, String pusher, String name, int state) {
        return smsDao.query(scene, pusher, name, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray scenes() {
        JSONArray scenes = new JSONArray();
        BeanFactory.getBeans(SmsScene.class).forEach(scene -> scene.scenes().forEach((key, value) -> {
            JSONObject object = new JSONObject();
            object.put("id", key);
            object.put("name", message.get(value));
            scenes.add(object);
        }));

        return scenes;
    }

    @Override
    public JSONArray pushers() {
        JSONArray pushers = new JSONArray();
        BeanFactory.getBeans(SmsPusher.class).forEach(pusher -> {
            JSONObject object = new JSONObject();
            object.put("id", pusher.key());
            object.put("name", message.get(pusher.name()));
            pushers.add(object);
        });

        return pushers;
    }

    @Override
    public void save(SmsModel sms) {
        SmsModel model = validator.isId(sms.getId()) ? smsDao.findById(sms.getId()) : null;
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
    public Object push(String scene, String mobile, String content) {
        SmsModel sms = smsDao.find(scene, 1);
        if (sms == null) {
            logger.warn(null, "无可用的SMS推送配置[{}:{}:{}]！", scene, mobile, content);

            return templates.get().failure(108901, message.get(SmsModel.NAME + ".null"), null, null);
        }

        sms.setTime(dateTime.now());
        smsDao.save(sms);

        SmsPusher pusher = pushers.get(sms.getPusher());
        if (pusher == null) {
            logger.warn(null, "无可用的SMS推送器[{}:{}:{}:{}]！", scene, sms.getPusher(), mobile, content);

            return templates.get().failure(108902, message.get(SmsModel.NAME + ".no-pusher"), null, null);
        }

        Object object = pusher.push(sms.getConfig(), mobile, content);
        if (logger.isInfoEnable())
            logger.info("发送[{}:{}]短信[{}:{}:{}:{}]。", scene, sms.getPusher(), mobile, sms.getConfig(), content, object);
        if (object == null)
            return templates.get().failure(108903, message.get(SmsModel.NAME + ".push.fail"), null, null);

        return object;
    }

    @Override
    public Object captcha(String scene, String mobile) {
        String key = SmsModel.NAME + ".captcha";
        String captcha = keyvalueService.value("setting.global.sms.captcha");
        if (!validator.isEmpty(captcha)) {
            session.set(key, captcha);

            return new JSONObject();
        }

        String prefix = key + ":" + TimeUnit.Minute.now() + ":";
        if (cache.get(prefix + mobile) != null || cache.get(prefix + header.getIp()) != null)
            return templates.get().failure(108911, message.get(SmsModel.NAME + ".frequently"), null, null);

        String code = numeric.toString(generator.random(100000, 999999));
        session.set(key, code);
        cache.put(prefix + mobile, code, false);
        cache.put(prefix + header.getIp(), code, false);

        return push(scene, mobile, code);
    }

    @Override
    public boolean captcha(String code) {
        String c = session.remove(SmsModel.NAME + ".captcha");

        return !validator.isEmpty(c) && c.equals(code);
    }

    @Override
    public int getContextRefreshedSort() {
        return 108;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(SmsPusher.class).forEach(pusher -> {
            pushers.put(pusher.key(), pusher);
            JSONObject lv = new JSONObject();
            lv.put("id", pusher.key());
            lv.put("name", pusher.name());
            pushersArray.add(lv);
        });
    }
}
