package org.lpw.clivia.push;

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
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(PushModel.NAME + ".service")
public class PushServiceImpl implements PushService, ContextRefreshedListener {
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
    private Json json;
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
    private PushDao pushDao;
    private final Map<String, PushSender> senders = new HashMap<>();

    @Override
    public JSONObject query(String scene, String sender, String name, int state) {
        return pushDao.query(scene, sender, name, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray scenes() {
        JSONArray scenes = new JSONArray();
        BeanFactory.getBeans(PushScene.class).forEach(scene -> scene.scenes().forEach((key, value) -> {
            JSONObject object = new JSONObject();
            object.put("id", key);
            object.put("name", message.get(value));
            scenes.add(object);
        }));

        return scenes;
    }

    @Override
    public JSONArray senders() {
        JSONArray senders = new JSONArray();
        BeanFactory.getBeans(PushSender.class).forEach(sender -> {
            JSONObject object = new JSONObject();
            object.put("id", sender.key());
            object.put("name", message.get(sender.name()));
            senders.add(object);
        });

        return senders;
    }

    @Override
    public void save(PushModel push) {
        PushModel model = validator.isId(push.getId()) ? pushDao.findById(push.getId()) : null;
        if (model == null) {
            push.setId(null);
            push.setTime(dateTime.now());
        } else
            push.setTime(model.getTime());
        pushDao.save(push);
    }

    @Override
    public void state(String id, int state) {
        pushDao.state(id, state);
    }

    @Override
    public void delete(String id) {
        pushDao.delete(id);
    }

    @Override
    public Object send(String scene, JSONObject args) {
        PushModel push = pushDao.find(scene, 1);
        if (push == null) {
            logger.warn(null, "无可用的推送配置[{}:{}]！", scene, args);

            return templates.get().failure(108901, message.get(PushModel.NAME + ".null"), null, null);
        }

        push.setTime(dateTime.now());
        pushDao.save(push);

        PushSender sender = senders.get(push.getSender());
        if (sender == null) {
            logger.warn(null, "无可用的推送器[{}:{}:{}]！", scene, push.getSender(), args);

            return templates.get().failure(108902, message.get(PushModel.NAME + ".no-sender"), null, null);
        }

        Object object = sender.push(json.toObject(push.getConfig()), args);
        if (logger.isInfoEnable())
            logger.info("推送[{}:{}:{}:{}:{}]。", scene, push.getSender(), push.getConfig(), args, object);
        if (object == null)
            return templates.get().failure(108903, message.get(PushModel.NAME + ".push.fail"), null, null);

        return object;
    }

    @Override
    public Object captcha(String scene, String mobile) {
        String key = PushModel.NAME + ".captcha";
        String captcha = keyvalueService.value("setting.global.sms.captcha");
        if (!validator.isEmpty(captcha)) {
            session.set(key, captcha);

            return new JSONObject();
        }

        String prefix = key + ":" + TimeUnit.Minute.now() + ":";
        if (cache.get(prefix + mobile) != null || cache.get(prefix + header.getIp()) != null)
            return templates.get().failure(108911, message.get(PushModel.NAME + ".frequently"), null, null);

        String code = numeric.toString(generator.random(100000, 999999));
        session.set(key, code);
        cache.put(prefix + mobile, code, false);
        cache.put(prefix + header.getIp(), code, false);

        JSONObject args = new JSONObject();
        args.put("mobile", mobile);
        args.put("content", code);

        return send(scene, args);
    }

    @Override
    public boolean captcha(String code) {
        String c = session.remove(PushModel.NAME + ".captcha");

        return !validator.isEmpty(c) && c.equals(code);
    }

    @Override
    public int getContextRefreshedSort() {
        return 108;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(PushSender.class).forEach(sender -> senders.put(sender.key(), sender));
    }
}
