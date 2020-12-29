package org.lpw.clivia.user;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.sms.CaptchaValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN_UP_CAPTCHA)
public class SignUpCaptchaValidatorImpl extends CaptchaValidatorSupport {
    @Inject private KeyvalueService keyvalueService;

    @Override
    protected boolean ignore(String parameter) {
        return super.ignore(parameter);
    }
}
