package org.lpw.clivia.transfer;

import com.alibaba.fastjson.JSONObject;

/**
 * 转账完成通知。
 *
 * @author lpw
 */
public interface TransferNotice {
    /**
     * 支付完成。
     *
     * @param transfer 转账信息。
     * @param notice   通知配置。
     */
    void transferDone(TransferModel transfer, JSONObject notice);
}
