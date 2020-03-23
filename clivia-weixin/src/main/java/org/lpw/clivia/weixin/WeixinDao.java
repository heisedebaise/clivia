package org.lpw.clivia.weixin;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface WeixinDao {
    PageList<WeixinModel> query();

    WeixinModel findByKey(String key);

    WeixinModel findByAppId(String appId);

    void save(WeixinModel weixin);

    void delete(String id);

    void close();
}
