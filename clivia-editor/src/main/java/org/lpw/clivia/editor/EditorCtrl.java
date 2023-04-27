package org.lpw.clivia.editor;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(EditorModel.NAME + ".ctrl")
@Execute(name = "/editor/", key = EditorModel.NAME, code = "114")
public class EditorCtrl {
    @Inject
    private Request request;
    @Inject
    private EditorService editorService;

    @Execute(name = "get", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object get() {
        return editorService.get(request.get("listener"), request.get("key"));
    }

    @Execute(name = "put", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object put() {
        return editorService.put(request.get("listener"), request.get("key"), request.get("id"), request.getAsJsonArray("lines"), request.getAsLong("sync"));
    }

    @Execute(name = "view", permit = Permit.always, type = Templates.FREEMARKER)
    public Object view() {
        return editorService.view(request.get("listener"), request.get("key"));
    }

    @Execute(name = "ai", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object ai() {
        return editorService.ai();
    }

    @Execute(name = "ai-text", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object aiText() {
        return editorService.aiText(request.get("content"));
    }

    @Execute(name = "ai-image", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object aiImage() {
        return editorService.aiImage(request.get("content"), request.getAsInt("count"));
    }
}
