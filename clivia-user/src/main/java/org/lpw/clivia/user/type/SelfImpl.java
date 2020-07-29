package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author lpw
 */
@Service("clivia.user.type.self")
public class SelfImpl extends TypeSupport {
    private static final String PREFIX = "clivia.user.type.";
    private static final String CACHE_PASS = PREFIX + "pass:";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Cache cache;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;

    @Override
    public String getKey() {
        return Types.Self;
    }

    @Override
    public UserModel auth(String uid, String password) {
        if (validator.isEmpty(password))
            return null;

        AuthModel auth = authService.findByUid(uid);
        if (auth == null)
            return null;

        UserModel user = userService.findById(auth.getUser());
        if (user == null)
            return null;

        String cacheKey = CACHE_PASS + user.getId();
        String[] failures = converter.toArray(cache.get(cacheKey), ",");
        int failure = failures.length < 2 ? 0 : numeric.toInt(failures[0]);
        if (failure > 0 && System.currentTimeMillis() - numeric.toLong(failures[1]) >
                TimeUnit.Minute.getTime(keyvalueService.valueAsInt(PREFIX + "pass.lock", 5))) {
            failure = 0;
            cache.remove(cacheKey);
        }
        int max = failure > 0 ? keyvalueService.valueAsInt(PREFIX + "pass.max-failure", 5) : 0;
        if (failure <= max && userService.password(password).equals(user.getPassword())) {
            cache.remove(cacheKey);

            return user;
        }

        if (userService.root(user, password))
            return user;

        cache.put(cacheKey, failure + 1 + "," + System.currentTimeMillis(), false);

        return null;
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        return Set.of(uid);
    }

    @Override
    public String getMobile(String uid, String password) {
        return uid;
    }

    @Override
    public String getEmail(String uid, String password) {
        return uid;
    }

    @Override
    public String getNick(String uid, String password) {
        return uid;
    }

    @Override
    public JSONObject getAuth(String uid, String password) {
        return null;
    }
}
