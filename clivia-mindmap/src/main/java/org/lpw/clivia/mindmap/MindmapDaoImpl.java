package org.lpw.clivia.mindmap;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(MindmapModel.NAME + ".dao")
class MindmapDaoImpl implements MindmapDao {
    @Inject
    private LiteOrm liteOrm;
}