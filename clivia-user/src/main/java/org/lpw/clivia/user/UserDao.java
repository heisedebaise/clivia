package org.lpw.clivia.user;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface UserDao {
    PageList<UserModel> query(String idcard, String name, String nick, String mobile, String email, String weixin, String qq, String code,
                              int minGrade, int maxGrade, int state, String register, int pageSize, int pageNum);

    UserModel findById(String id);

    UserModel findByCode(String code);

    int count();

    int count(Timestamp[] register);

    void save(UserModel user);

    void state(String id, int state);

    void delete(String id);
}
