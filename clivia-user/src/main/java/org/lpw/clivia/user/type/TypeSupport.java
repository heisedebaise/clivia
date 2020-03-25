package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
abstract class TypeSupport implements Type {
    @Override
    public String getMobile(String uid, String password) {
        return null;
    }

    @Override
    public String getEmail(String uid, String password) {
        return null;
    }

    @Override
    public String getNick(String uid, String password) {
        return null;
    }

    @Override
    public String getPortrait(String uid, String password) {
        return null;
    }

    protected String get(String uid, String password, String name) {
        JSONObject object = getAuth(uid, password);

        return object == null || !object.containsKey(name) ? null : object.getString(name);
    }
}
