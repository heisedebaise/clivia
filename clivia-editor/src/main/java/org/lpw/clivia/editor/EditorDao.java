package org.lpw.clivia.editor;

import org.lpw.photon.dao.orm.PageList;

interface EditorDao {
    PageList<EditorModel> query(String key);

    EditorModel findById(String id);

    EditorModel findByKey(String key);

    void insert(EditorModel editor);

    void save(EditorModel editor, boolean close);

    void delete(String id);
}