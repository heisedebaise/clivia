package org.lpw.clivia.user.info;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(InfoModel.NAME + ".ctrl")
@Execute(name = "/user/info/", key = InfoModel.NAME, code = "151")
public class InfoCtrl {
    @Inject
    private Request request;
    @Inject
    private InfoService infoService;

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return infoService.user();
    }

    @Execute(name = "find", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object find() {
        return infoService.find(request.get("name"));
    }

    @Execute(name = "save", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "value", failureCode = 5),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object save() {
        infoService.save(request.get("name"), request.get("value"));

        return "";
    }

    @Execute(name = "saves", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object saves() {
        request.getMap().forEach(infoService::save);

        return "";
    }
}