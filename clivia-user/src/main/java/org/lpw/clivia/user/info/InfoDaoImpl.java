package org.lpw.clivia.user.info;

import org.lpw.photon.dao.jdbc.Sql;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Repository(InfoModel.NAME + ".dao")
class InfoDaoImpl implements InfoDao {
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<InfoModel> query(String user) {
        return liteOrm.query(new LiteQuery(InfoModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public InfoModel find(String user, String name) {
        return liteOrm.findOne(new LiteQuery(InfoModel.class).where("c_user=? and c_name=?"), new Object[]{user, name});
    }

    @Override
    public Set<String> user(String name, String value) {
        Set<String> set = new HashSet<>();
        sql.query("select c_user from t_user_info where c_name=? and c_value like ?",
                new Object[]{name, "%" + value + "%"}).forEach(list -> set.add((String) list.get(0)));

        return set;
    }

    @Override
    public int count() {
        return liteOrm.count(new LiteQuery(InfoModel.class), null);
    }

    @Override
    public void save(InfoModel info) {
        liteOrm.save(info);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(InfoModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public void close() {
        liteOrm.close();
    }
}