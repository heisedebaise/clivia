package org.lpw.clivia.sms;

public interface SmsPusher {
    /**
     * 引用KEY。
     *
     * @return 引用KEY。
     */
    String key();

    /**
     * 推送。
     *
     * @param config  配置。
     * @param mobile  手机号。
     * @param content 短信内容。
     * @return 结果。
     */
    String push(String config, String mobile, String content);
}
