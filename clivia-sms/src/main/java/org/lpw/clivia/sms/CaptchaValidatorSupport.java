package org.lpw.clivia.sms;

import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;

import javax.inject.Inject;

public class CaptchaValidatorSupport extends ValidatorSupport {
    @Inject
    private Session session;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if(ignore(parameter))
            return true;

        String code = session.remove(SmsModel.NAME + ".captcha");

        return !validator.isEmpty(code) && code.equals(parameter);
    }

    protected boolean ignore(String parameter){
        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return SmsModel.NAME + ".captcha.illegal";
    }
}
