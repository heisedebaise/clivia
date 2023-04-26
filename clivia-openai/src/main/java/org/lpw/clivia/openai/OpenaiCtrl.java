package org.lpw.clivia.openai;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(OpenaiModel.NAME + ".ctrl")
@Execute(name = "/openai/", key = OpenaiModel.NAME, code = "164")
public class OpenaiCtrl {
    @Inject
    private Request request;
    @Inject
    private OpenaiService openaiService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return openaiService.query(request.get("key"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "authorization", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "authorization", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "organization", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "chat", failureCode = 8),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = OpenaiService.KEY_NOT_EXISTS_VALIDATOR, parameters = {"id", "key"}, failureCode = 9)
    })
    public Object save() {
        openaiService.save(request.setToModel(OpenaiModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        openaiService.delete(request.get("id"));

        return "";
    }
}