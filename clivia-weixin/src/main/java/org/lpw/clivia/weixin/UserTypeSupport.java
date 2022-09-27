package org.lpw.clivia.weixin;

import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.type.TypeSupport;
import org.lpw.clivia.weixin.info.InfoModel;
import org.lpw.clivia.weixin.info.InfoService;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Validator;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public abstract class UserTypeSupport extends TypeSupport {
    @Inject
    protected Context context;
    @Inject
    protected Validator validator;
    @Inject
    protected UserService userService;
    @Inject
    protected AuthService authService;
    @Inject
    protected WeixinService weixinService;
    @Inject
    protected InfoService infoService;

    @Override
    public UserModel auth(String uid, String password, String grade) {
        Set<String> set = getUid(uid, password);
        if (set == null)
            return null;

        UserModel user = null;
        for (String u : set) {
            AuthModel auth = authService.findByUid(u);
            if (auth != null && (user = userService.findById(auth.getUser())) != null)
                break;
        }

        if (user == null)
            return userService.signUp(uid, password, getKey(), null, grade, getInvitecode());

        for (String u : set)
            if (authService.findByUid(u) == null)
                authService.create(user.getId(), u, getKey(), getMobile(uid, password), getEmail(uid, password), getNick(uid, password), getAvatar(uid, password));

        return user;
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        String openId = get(uid, password, "openid");
        if (openId == null)
            return null;

        Set<String> set = new HashSet<>();
        set.add(openId);
        InfoModel info = infoService.find(openId);
        if (!validator.isEmpty(info.getUnionId())) {
            set.add(info.getUnionId());
            infoService.query(info.getUnionId()).forEach(i -> set.add(i.getOpenId()));
        }

        return set;
    }

    protected String key(String uid, String password) {
        return WeixinModel.NAME + ".uid-password." + uid + "-" + password;
    }
}
