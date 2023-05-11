package org.lpw.clivia.aliyun;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AliyunModel.NAME + ".ctrl")
@Execute(name = "/aliyun/", key = AliyunModel.NAME, code = "116")
public class AliyunCtrl {
    @Inject
    private Request request;
    @Inject
    private AliyunService aliyunService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return aliyunService.query(request.get("key"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "accessKeyId", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "accessKeySecret", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "regionId", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "endpoint", failureCode = 9),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = AliyunService.KEY_NOT_EXISTS_VALIDATOR, parameters = {"id", "key"}, failureCode = 10)
    })
    public Object save() {
        aliyunService.save(request.setToModel(AliyunModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        aliyunService.delete(request.get("id"));

        return "";
    }
}