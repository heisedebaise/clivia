package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service("clivia.user.type.self")
public class SelfImpl implements Type {
    @Inject
    private UserService userService;

    @Override
    public int getKey() {
        return Types.SELF;
    }

    @Override
    public String getUid(String uid, String password) {
        return uid;
    }

    @Override
    public void signUp(UserModel user, String uid, String password) {
        user.setPassword(userService.password(password));
    }

    @Override
    public JSONObject getAuth(String uid, String password) {
        return null;
    }
}
