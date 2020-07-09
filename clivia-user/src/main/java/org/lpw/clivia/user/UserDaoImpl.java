package org.lpw.clivia.user;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

/**
 * @author lpw
 */
@Repository(UserModel.NAME + ".dao")
class UserDaoImpl implements UserDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<UserModel> query(String idcard, String name, String nick, String mobile, String email, String code,
                                     int minGrade, int maxGrade, int state, String register, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_code", DaoOperation.Equals, code)
                .where("c_mobile", DaoOperation.Equals, mobile)
                .where("c_idcard", DaoOperation.Equals, idcard)
                .like(null, "c_name", name)
                .like(null, "c_nick", nick)
                .like(null, "c_email", email)
                .where("c_grade", DaoOperation.GreaterEquals, minGrade)
                .where("c_grade", DaoOperation.LessEquals, maxGrade)
                .where("c_state", DaoOperation.Equals, state)
                .between("c_register", ColumnType.Timestamp, register)
                .order("c_register desc")
                .query(UserModel.class, pageSize, pageNum);
    }

    @Override
    public UserModel findById(String id) {
        return liteOrm.findById(UserModel.class, id);
    }

    @Override
    public UserModel findByCode(String code) {
        return liteOrm.findOne(new LiteQuery(UserModel.class).where("c_code=?"), new Object[]{code});
    }

    @Override
    public int count() {
        return liteOrm.count(new LiteQuery(UserModel.class).where("c_grade<?"), new Object[]{99});
    }

    @Override
    public int count(Timestamp[] register) {
        return liteOrm.count(new LiteQuery(UserModel.class).where("c_register between ? and ? and c_grade<?"), new Object[]{register[0], register[1], 99});
    }

    @Override
    public void save(UserModel user) {
        liteOrm.save(user);
    }
}
