package org.lpw.clivia.editor;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(EditorModel.NAME + ".ctrl")
@Execute(name = "/editor/", key = EditorModel.NAME, code = "114")
public class EditorCtrl {
    @Inject
    private Request request;
    @Inject
    private EditorService editorService;

    @Execute(name = "query", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
    })
    public Object query() {
        return editorService.query(request.get("key"));
    }

    @Execute(name = "save", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 4)
    })
    public Object save() {
        return editorService.save(request.get("key"), request.getAsJsonObject("content"));
    }

    @Execute(name = "order", permit = Permit.always, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1)
    })
    public Object order() {
        editorService.order(request.get("id"), request.getAsInt("order"));

        return "";
    }

    @Execute(name = "delete", permit = Permit.always, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1)
    })
    public Object delete() {
        editorService.delete(request.get("id"));

        return "";
    }
}