package org.lpw.clivia.sms;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;

import javax.inject.Inject;

public class CaptchaValidatorSupport extends ValidatorSupport {
    @Inject
    private SmsService smsService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return ignore(parameter) || smsService.captcha(parameter);
    }

    protected boolean ignore(String parameter) {
        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return SmsModel.NAME + ".captcha.illegal";
    }
}
