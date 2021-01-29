package org.lpw.clivia.weixin.qrcode;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface QrcodeDao {
    PageList<QrcodeModel> query(String key, String appId, String user, String name, String scene, String time, int pageSize, int pageNum);

    QrcodeModel find(String key, String user, String name);

    void save(QrcodeModel qrcode);

    void delete(String id);
}
