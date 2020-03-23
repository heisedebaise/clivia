package org.lpw.clivia.console;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(ConsoleModel.NAME + ".ctrl")
@Execute(name = "/console/", key = ConsoleModel.NAME, code = "191")
public class ConsoleCtrl {
    @Inject
    private Request request;
    @Inject
    private MenuHelper menuHelper;
    @Inject
    private MetaHelper metaHelper;

    @Execute(name = "menu", validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object menu() {
        return menuHelper.get(request.getAsBoolean("all"));
    }

    @Execute(name = "meta", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2)
    })
    public Object meta() {
        return metaHelper.get(request.get("key"), false);
    }
}
