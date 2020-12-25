package org.lpw.clivia.sms;

import org.lpw.photon.util.Http;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(SmsModel.NAME + ".pusher.link")
public class LinkSmsPusherImpl implements SmsPusher {
    @Inject
    private Http http;

    @Override
    public String key() {
        return "key";
    }

    @Override
    public boolean push(String config, String mobile, String content) {
        return false;
    }
}
