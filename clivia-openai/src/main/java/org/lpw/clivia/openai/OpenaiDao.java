package org.lpw.clivia.openai;

import org.lpw.photon.dao.orm.PageList;

interface OpenaiDao {
    PageList<OpenaiModel> query(String key, int pageSize, int pageNum);

    OpenaiModel findById(String id);

    OpenaiModel findByKey(String key);

    void save(OpenaiModel openai);

    void delete(String id);
}