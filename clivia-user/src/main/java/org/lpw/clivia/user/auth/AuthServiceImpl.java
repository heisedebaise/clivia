package org.lpw.clivia.user.auth;

import com.alibaba.fastjson.JSONArray;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(AuthModel.NAME + ".service")
public class AuthServiceImpl implements AuthService {
    private static final String CACHE_UID = AuthModel.NAME + ".service.uid:";

    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private UserService userService;
    @Inject
    private AuthDao authDao;

    @Override
    public JSONArray query(String user) {
        return modelHelper.toJson(authDao.query(user).getList());
    }

    @Override
    public List<AuthModel> list(String user) {
        return authDao.query(user).getList();
    }

    @Override
    public Set<String> users(String uid) {
        if (validator.isEmpty(uid))
            return null;

        Set<String> set = new HashSet<>();
        authDao.search(uid).getList().forEach(auth -> set.add(auth.getUser()));
        if (set.isEmpty())
            set.add(uid);

        return set;
    }

    @Override
    public AuthModel create(String userId, String uid, String type, String mobile, String email, String nick, String avatar) {
        AuthModel auth = new AuthModel();
        auth.setUser(userId);
        auth.setUid(uid);
        save(auth, type, mobile, email, nick, avatar);

        return auth;
    }

    @Override
    public void update(AuthModel auth, String type, String mobile, String email, String nick, String avatar) {
        if (equals(auth.getType(), type) && equals(auth.getMobile(), mobile) && equals(auth.getEmail(), email)
                && equals(auth.getNick(), nick) && equals(auth.getAvatar(), avatar))
            return;

        save(auth, type, mobile, email, nick, avatar);
    }

    private void save(AuthModel auth, String type, String mobile, String email, String nick, String avatar) {
        auth.setType(type);
        auth.setMobile(mobile);
        auth.setEmail(email);
        auth.setNick(nick);
        auth.setAvatar(avatar);
        auth.setTime(dateTime.now());
        authDao.save(auth);
    }

    private boolean equals(String get, String set) {
        if (get == null)
            return set == null;

        return get.equals(set);
    }

    @Override
    public AuthModel findByUid(String uid) {
        return validator.isEmpty(uid) ? null : cache.computeIfAbsent(CACHE_UID + uid, key -> authDao.findByUid(uid), false);
    }

    @Override
    public String findUser(String uid, String defaultUser) {
        AuthModel auth = findByUid(uid);

        return auth == null ? defaultUser : auth.getUser();
    }

    @Override
    public void delete() {
        AuthModel auth = findByUid(userService.uidFromSession());
        authDao.delete(auth);
        cache.remove(CACHE_UID + auth.getUid());
        userService.clearCache();
        userService.signOut();
    }

    @Override
    public void delete(String user) {
        authDao.query(user).getList().forEach(auth -> cache.remove(CACHE_UID + auth.getUid()));
        authDao.delete(user);
    }
}
