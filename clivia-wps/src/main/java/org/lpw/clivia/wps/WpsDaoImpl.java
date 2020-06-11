package org.lpw.clivia.wps;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(WpsModel.NAME + ".dao")
class WpsDaoImpl implements WpsDao {
    @Inject
    private LiteOrm liteOrm;
}
