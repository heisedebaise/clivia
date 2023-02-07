package org.lpw.clivia.editor;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(EditorModel.NAME + ".dao")
class EditorDaoImpl implements EditorDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<EditorModel> query(String key) {
        return liteOrm.query(new LiteQuery(EditorModel.class).where("c_key=?").order("c_order,c_time desc"), new Object[]{key});
    }

    @Override
    public EditorModel findById(String id) {
        return liteOrm.findById(EditorModel.class, id);
    }

    @Override
    public EditorModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(EditorModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void insert(EditorModel editor) {
        liteOrm.insert(editor);
        liteOrm.close();
    }

    @Override
    public void save(EditorModel editor, boolean close) {
        liteOrm.save(editor);
        if (close)
            liteOrm.close();
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(EditorModel.class, id);
        liteOrm.close();
    }
}