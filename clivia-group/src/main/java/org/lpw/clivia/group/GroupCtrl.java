package org.lpw.clivia.group;

import javax.inject.Inject;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.springframework.stereotype.Controller;

@Controller(GroupModel.NAME + ".ctrl")
@Execute(name = "/group/", key = GroupModel.NAME, code = "159")
public class GroupCtrl {
    @Inject
    private Request request;
    @Inject
    private GroupService groupService;

    @Execute(name = "friends", permit = "0", validates = { @Validate(validator = UserService.VALIDATOR_SIGN) })
    public Object friends() {
        return groupService.friends();
    }

    @Execute(name = "find", permit = "0", validates = { @Validate(validator = UserService.VALIDATOR_SIGN) })
    public Object find() {
        return groupService.find(request.get("idUidCode"));
    }
}