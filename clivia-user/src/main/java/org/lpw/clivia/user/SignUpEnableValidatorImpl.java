package org.lpw.clivia.user;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN_UP_ENABLE)
public class SignUpEnableValidatorImpl extends ValidatorSupport {
    @Inject
    private KeyvalueService keyvalueService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return keyvalueService.valueAsInt("setting.global.sign-up.enable", 0) == 1;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".sign-up.disable";
    }
}
