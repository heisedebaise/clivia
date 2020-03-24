package org.lpw.clivia.alipay;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface AlipayDao {
    PageList<AlipayModel> query();

    AlipayModel findByKey(String key);

    AlipayModel findByAppId(String appId);

    void save(AlipayModel alipay);

    void delete(String id);
}
