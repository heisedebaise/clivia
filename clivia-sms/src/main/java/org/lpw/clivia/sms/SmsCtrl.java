package org.lpw.clivia.sms;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(SmsModel.NAME + ".ctrl")
@Execute(name = "/sms/", key = SmsModel.NAME, code = "108")
public class SmsCtrl {
    @Inject
    private Request request;
    @Inject
    private SmsService smsService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return smsService.query(request.get("scene"), request.get("pusher"), request.get("name"), request.getAsInt("state", -1));
    }

    @Execute(name = "lvs", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object lvs() {
        return smsService.lvs();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "scene", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "scene", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "pusher", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "pusher", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "config", failureCode = 7),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 8),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        smsService.save(request.setToModel(SmsModel.class));

        return "";
    }

    @Execute(name = "state", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 8),
            @Validate(validator = Validators.SIGN)
    })
    public Object state() {
        smsService.state(request.get("id"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        smsService.delete(request.get("id"));

        return "";
    }
}
