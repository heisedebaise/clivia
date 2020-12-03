package org.lpw.clivia.user;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SECRET)
public class SecretValidatorImpl extends ValidatorSupport {
    @Inject
    private UserService userService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return userService.password(parameter).equals(userService.findById(userService.id()).getSecret());
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".secret.failure";
    }
}
