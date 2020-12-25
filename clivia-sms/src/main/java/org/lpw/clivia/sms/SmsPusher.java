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
     * @param mobile  手机号。
     * @param content 短信内容。
     * @return 推送结果：true-成功；false-失败。
     */
    boolean push(String mobile, String content);
}
