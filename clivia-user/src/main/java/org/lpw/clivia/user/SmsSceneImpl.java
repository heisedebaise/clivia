package org.lpw.clivia.user;

import java.util.Map;

import javax.inject.Inject;

import org.lpw.clivia.sms.SmsScene;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Service;

@Service(UserModel.NAME + ".sms.scene")
public class SmsSceneImpl implements SmsScene {
    @Inject
    private Message message;

    @Override
    public Map<String, String> scenes() {
        return Map.of(UserService.SMS_SIGN_IN, message.get(UserModel.NAME + ".sign-in"), UserService.SMS_SIGN_UP,
                message.get(UserModel.NAME + ".sign-up"));
    }
}
