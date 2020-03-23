package org.lpw.clivia.user;

import org.lpw.clivia.user.type.Types;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(UserService.VALIDATOR_PASSWORD)
public class PasswordValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return !validator.isEmpty(parameters[0]) || numeric.toInt(parameters[1]) != Types.SELF;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".password.empty";
    }
}
