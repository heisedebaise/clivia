package org.lpw.clivia.group;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(GroupModel.NAME + ".ctrl")
@Execute(name = "/group/", key = GroupModel.NAME, code = "159")
public class GroupCtrl {
    @Inject
    private Request request;
    @Inject
    private GroupService groupService;

    @Execute(name = "friend", permit = "0", validates = { @Validate(validator = UserService.VALIDATOR_SIGN) })
    public Object friend() {
        return groupService.friend();
    }
}