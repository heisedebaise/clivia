package org.lpw.clivia.update;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(UpdateModel.NAME + ".ctrl")
@Execute(name = "/update/", key = UpdateModel.NAME, code = "107")
public class UpdateCtrl {
    @Inject
    private Request request;
    @Inject
    private UpdateService updateService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return updateService.query();
    }

    @Execute(name = "latest", validates = {
            @Validate(validator = Validators.GREATER_THAN, number = {-1}, parameter = "version", failureCode = 2),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "client", failureCode = 4)
    })
    public Object latest() {
        return updateService.latest(request.getAsInt("version"), request.getAsInt("client"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "version", failureCode = 2),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "forced", failureCode = 3),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "client", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "explain", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "url", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "url", failureCode = 7),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        updateService.save(request.setToModel(UpdateModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        updateService.delete(request.get("id"));

        return "";
    }
}
