package org.lpw.clivia.sms;

import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Http;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(SmsModel.NAME + ".pusher.link")
public class LinkSmsPusherImpl implements SmsPusher {
    @Inject
    private Http http;
    @Inject
    private Codec codec;

    @Override
    public String key() {
        return "link";
    }

    @Override
    public String push(String config, String mobile, String content) {
        return http.get(config.replaceAll("MOBILE", mobile).replaceAll("CONTENT", codec.encodeUrl(content, null)), null, "");
    }
}
