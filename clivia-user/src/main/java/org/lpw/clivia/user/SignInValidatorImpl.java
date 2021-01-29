package org.lpw.clivia.user;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.lpw.photon.util.Converter;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN_IN)
public class SignInValidatorImpl extends ValidatorSupport {
    @Inject
    private Converter converter;
    @Inject
    private UserService userService;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return userService.signIn(parameters[0], parameters[1], parameters[2], parameters[3]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".sign-in.failure";
    }
}
