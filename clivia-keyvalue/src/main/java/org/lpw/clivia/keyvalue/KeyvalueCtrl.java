package org.lpw.clivia.keyvalue;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(KeyvalueModel.NAME + ".ctrl")
@Execute(name = "/keyvalue/", key = KeyvalueModel.NAME, code = "101")
public class KeyvalueCtrl {
    @Inject
    private Request request;
    @Inject
    private KeyvalueService keyvalueService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return keyvalueService.query(request.get("key"));
    }

    @Execute(name = "list", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2)
    })
    public Object list() {
        return keyvalueService.list(request.get("key"));
    }

    @Execute(name = "object", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2)
    })
    public Object object() {
        return keyvalueService.object(request.get("key"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = KeyvalueService.VALIDATOR_KEY_NOT_EXISTS, parameters = {"id", "key"}, failureCode = 4)
    })
    public Object save() {
        keyvalueService.save(request.setToModel(KeyvalueModel.class));

        return "";
    }

    @Execute(name = "saves", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object saves() {
        keyvalueService.saves(request.getAsJsonArray("kvs"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        keyvalueService.delete(request.get("id"));

        return "";
    }
}
