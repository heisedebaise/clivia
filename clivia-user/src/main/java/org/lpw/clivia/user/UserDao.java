package org.lpw.clivia.user;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

/**
 * @author lpw
 */
interface UserDao {
    PageList<UserModel> query(String idcard, String name, String nick, String mobile, String email, String code,
                              int minGrade, int maxGrade, int state, Timestamp[] register, int pageSize, int pageNum);

    UserModel findById(String id);

    UserModel findByCode(String code);

    int count();

    void save(UserModel user);
}
