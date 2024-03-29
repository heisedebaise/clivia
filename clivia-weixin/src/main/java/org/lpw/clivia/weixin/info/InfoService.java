package org.lpw.clivia.weixin.info;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public interface InfoService {
    /**
     * 检索。
     *
     * @return 数据集。
     */
    JSONArray query();

    /**
     * 检索。
     *
     * @param unionId Union ID。
     * @return 数据集。
     */
    List<InfoModel> query(String unionId);

    /**
     * 查找Open ID。
     *
     * @param appId APP ID。
     * @param id    微信Open ID或Union ID。
     * @return Open ID。
     */
    String findOpenId(String appId, String id);

    /**
     * 查找信息。
     *
     * @param openId Open ID。
     * @return 信息，不存在则返回null。
     */
    InfoModel find(String openId);

    /**
     * 查找用户的Open ID。
     *
     * @param appId App ID。
     * @param user  用户ID。
     * @return Open ID。
     */
    String findUserOpenId(String appId, String user);

    /**
     * 保存详情信息。
     *
     * @param key     引用KEY。
     * @param appId   APP ID。
     * @param unionId Union ID。
     * @param openId  Open ID。
     * @return Union ID。
     */
    String save(String key, String appId, String unionId, String openId);
}
