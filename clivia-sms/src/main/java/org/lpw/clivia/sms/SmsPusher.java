package org.lpw.clivia.sms;

public interface SmsPusher {
    /**
     * 引用KEY。
     *
     * @return 引用KEY。
     */
    String key();

    /**
     * 名称。
     *
     * @return 名称。
     */
    String name();

    /**
     * 推送。
     *
     * @param config  配置。
     * @param mobile  手机号。
     * @param content 短信内容。
     * @return 推送结果，null-失败；空JSON-成功；非空JSON-包含错误码和错误信息。
     */
    Object push(String config, String mobile, String content);
}
