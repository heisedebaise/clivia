package org.lpw.clivia.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.online.OnlineModel;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.clivia.user.type.Types;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service(UserModel.NAME + ".service")
public class UserServiceImpl implements UserService {
    private static final String CACHE_MODEL = UserModel.NAME + ".service.model:";
    private static final String CACHE_JSON = UserModel.NAME + ".service.json:";
    private static final String SESSION = UserModel.NAME + ".service.session";
    private static final String SESSION_INVITER = UserModel.NAME + ".service.session.inviter";
    private static final String SESSION_AUTH3 = UserModel.NAME + ".service.session.auth3";
    private static final String SESSION_UID = UserModel.NAME + ".service.session.uid";

    @Inject
    private Cache cache;
    @Inject
    private Digest digest;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private Types types;
    @Inject
    private AuthService authService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private UserDao userDao;
    private int codeLength = 8;

    @Override
    public String inviter(String code) {
        if (!validator.isEmpty(code))
            session.set(SESSION_INVITER, code);

        return session.get(SESSION_INVITER);
    }

    @Override
    public UserModel signUp(String uid, String password, String type) {
        UserModel user = fromSession();
        if (user == null)
            user = new UserModel();
        if (user.getRegister() == null)
            user.setRegister(dateTime.now());
        for (int i = 0; i < 1024 && user.getCode() == null; i++) {
            String code = generator.random(codeLength);
            if (userDao.findByCode(code) == null)
                user.setCode(code);
        }
        String mobile = types.getMobile(type, uid, password);
        if (!validator.isEmpty(mobile) && validator.isMobile(mobile)) {
            if (validator.isEmpty(user.getMobile()))
                user.setMobile(mobile);
        } else
            mobile = null;
        String email = types.getEmail(type, uid, password);
        if (!validator.isEmpty(email) && validator.isEmail(email)) {
            if (validator.isEmpty(user.getEmail()))
                user.setEmail(email);
        } else
            email = null;
        String portrait = types.getPortrait(type, uid, password);
        if (validator.isEmpty(user.getPortrait()))
            user.setPortrait(portrait);
        String nick = types.getNick(type, uid, password);
        if (validator.isEmpty(user.getNick()))
            user.setNick(nick);
        setInviter(user);
        userDao.save(user);
        for (String ruid : types.getUid(type, uid, password))
            if (authService.findByUid(ruid) == null)
                authService.create(user.getId(), ruid, type, mobile, email, nick, portrait);
        clearCache(user);
        signIn(user, uid);

        return user;
    }

    private void setInviter(UserModel user) {
        if (!validator.isEmpty(user.getInviter()))
            return;

        String code = inviter(null);
        if (validator.isEmpty(code) || code.length() != codeLength)
            return;

        UserModel inviter = userDao.findByCode(code.toLowerCase());
        if (inviter == null)
            return;

        user.setInviter(inviter.getId());
        inviter.setInviteCount(inviter.getInviteCount() + 1);
        userDao.save(inviter);
        clearCache(inviter);
    }

    @Override
    public boolean signIn(String uid, String password, String type) {
        UserModel user = types.auth(type, uid, password);
        if (user == null)
            return false;

        signIn(user, uid);
        if (!Types.Self.equals(type))
            session.set(SESSION_AUTH3, types.getAuth(type, uid, password));

        return true;
    }

    private void signIn(UserModel user, String uid) {
        onlineService.signIn(user);
        session.set(SESSION, user);
        session.set(SESSION_UID, uid);
    }

    @Override
    public JSONObject sign() {
        if (!onlineService.isSign())
            return new JSONObject();

        UserModel user = fromSession();
        if (user == null)
            return new JSONObject();

        JSONObject object = getJson(user.getId(), user);
        JSONObject auth3 = session.get(SESSION_AUTH3);
        if (auth3 != null)
            object.put("auth3", auth3);

        return object;
    }

    @Override
    public String id() {
        UserModel user = fromSession();

        return user == null ? null : user.getId();
    }

    @Override
    public int grade() {
        UserModel user = fromSession();

        return user == null ? 0 : user.getGrade();
    }

    @Override
    public void signOut() {
        onlineService.signOut();
        session.remove(SESSION);
        session.remove(SESSION_AUTH3);
        session.remove(SESSION_UID);
    }

    @Override
    public void signOut(String sid) {
        session.remove(sid, SESSION);
        session.remove(sid, SESSION_AUTH3);
        session.remove(sid, SESSION_UID);
    }

    @Override
    public void modify(UserModel user) {
        session.set(SESSION, save(fromSession().getId(), user));
    }

    @Override
    public boolean password(String oldPassword, String newPassword) {
        UserModel user = fromSession();
        if (!validator.isEmpty(user.getPassword()) && !user.getPassword().equals(password(oldPassword)))
            return false;

        user.setPassword(password(newPassword));
        save(user);
        session.set(SESSION, user);

        return true;
    }

    @Override
    public String password(String password) {
        return digest.md5(UserModel.NAME + digest.sha1(password + UserModel.NAME));
    }

    @Override
    public UserModel fromSession() {
        UserModel user = session.get(SESSION);
        if (user == null) {
            OnlineModel online = onlineService.findBySid(session.getId());
            if (online != null)
                session.set(SESSION, user = findById(online.getUser()));
        }

        return user;
    }

    @Override
    public String uidFromSession() {
        return session.get(SESSION_UID);
    }

    @Override
    public JSONObject get(String[] ids) {
        JSONObject object = new JSONObject();
        for (String id : ids) {
            JSONObject user = getJson(id, null);
            if (user.isEmpty())
                continue;

            object.put(id, user);
        }

        return object;
    }

    @Override
    public JSONObject get(String id) {
        return getJson(id, null);
    }

    @Override
    public UserModel findById(String id) {
        String cacheKey = CACHE_MODEL + id;
        UserModel user = cache.get(cacheKey);
        if (user == null)
            cache.put(cacheKey, user = userDao.findById(id), false);

        return user;
    }

    @Override
    public JSONObject findByCode(String code) {
        String cacheKey = CACHE_JSON + code;
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            UserModel user = userDao.findByCode(code);
            cache.put(cacheKey, object = user == null ? new JSONObject() : getJson(user.getId(), user), false);
        }

        return object;
    }

    @Override
    public JSONObject findByUid(String uid) {
        String cacheKey = CACHE_JSON + uid;
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            UserModel user = findById(authService.findByUid(uid).getUser());
            cache.put(cacheKey, object = user == null ? new JSONObject() : getJson(user.getId(), user), false);
        }

        return object;
    }

    @Override
    public JSONObject findOrSign(String idUidCode) {
        UserModel user = findById(idUidCode);
        if (user == null) {
            AuthModel auth = authService.findByUid(idUidCode);
            if (auth != null)
                user = findById(auth.getUser());
        }
        if (user == null)
            user = userDao.findByCode(idUidCode);

        return user == null ? sign() : modelHelper.toJson(user);
    }

    @Override
    public JSONArray fill(JSONArray array, String[] names) {
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            for (String name : names)
                object.put(name, getJson(object.getString(name), null));
        }

        return array;
    }

    private JSONObject getJson(String id, UserModel user) {
        String cacheKey = CACHE_JSON + id;
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            if (user == null)
                user = findById(id);
            if (user == null)
                object = new JSONObject();
            else {
                object = modelHelper.toJson(user);
                object.put("auth", authService.query(user.getId()));
            }
            cache.put(cacheKey, object, false);
        }

        return object;
    }

    @Override
    public JSONObject query(String uid, String idcard, String name, String nick, String mobile, String email, String code,
                            int minGrade, int maxGrade, int state, String register) {
        if (validator.isEmpty(uid))
            return userDao.query(idcard, name, nick, mobile, email, code, minGrade, maxGrade, state, register,
                    pagination.getPageSize(20), pagination.getPageNum()).toJson();

        AuthModel auth = authService.findByUid(uid);
        if (auth == null)
            return BeanFactory.getBean(PageList.class).setPage(0, pagination.getPageSize(20), 0).toJson();

        JSONObject object = BeanFactory.getBean(PageList.class).setPage(1, pagination.getPageSize(20), 1).toJson();
        object.getJSONArray("list").add(getJson(auth.getUser(), null));

        return object;
    }

    @Override
    public void update(UserModel user) {
        save(user.getId(), user);
    }

    private UserModel save(String id, UserModel user) {
        UserModel model = findById(id);
        model.setIdcard(user.getIdcard());
        model.setName(user.getName());
        model.setNick(user.getNick());
        model.setMobile(user.getMobile());
        model.setEmail(user.getEmail());
        model.setPortrait(user.getPortrait());
        model.setGender(user.getGender());
        model.setBirthday(user.getBirthday());
        save(model);

        return model;
    }

    @Override
    public String resetPassword(String id) {
        String password = generator.random(8);
        UserModel user = findById(id);
        user.setPassword(password(password));
        save(user);

        return password;
    }

    @Override
    public void grade(String id, int grade) {
        UserModel user = findById(id);
        user.setGrade(grade);
        save(user);
    }

    @Override
    public void state(String id, int state) {
        UserModel user = findById(id);
        user.setState(state);
        save(user);
        if (state == 1)
            onlineService.signOutUser(id);
    }

    @Override
    public boolean root(UserModel user, String password) {
        if (!user.getCode().equals("99999999") || !validator.isEmpty(user.getPassword()) || userDao.count() > 2)
            return false;

        user.setPassword(password(password));
        save(user);

        return true;
    }

    private void save(UserModel user) {
        userDao.save(user);
        clearCache(user);
    }

    @Override
    public JSONObject count() {
        JSONObject object = new JSONObject();
        object.put("total", userDao.count());
        object.put("online", onlineService.count());

        return object;
    }

    @Override
    public void clearCache() {
        clearCache(fromSession());
    }

    private void clearCache(UserModel user) {
        cache.remove(CACHE_MODEL + user.getId());
        cache.remove(CACHE_JSON + user.getId());
        cache.remove(CACHE_JSON + user.getCode());
    }
}
