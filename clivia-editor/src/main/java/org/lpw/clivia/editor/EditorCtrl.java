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

    @Execute(name = "get", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
    })
    public Object get() {
        return editorService.get(request.get("listener"), request.get("key"));
    }

    @Execute(name = "put", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
    })
    public Object put() {
        return editorService.put(request.get("listener"), request.get("key"), request.get("id"), request.getAsJsonArray("lines"), request.getAsLong("sync"));
    }

    @Execute(name = "view", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
    })
    public Object view() {
        return editorService.get(request.get("listener"), request.get("key"));
    }
}
