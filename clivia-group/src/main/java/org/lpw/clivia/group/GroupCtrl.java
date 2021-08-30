package org.lpw.clivia.group;

import javax.inject.Inject;

import org.lpw.clivia.Permit;
import org.lpw.clivia.group.member.MemberService;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

@Controller(GroupModel.NAME + ".ctrl")
@Execute(name = "/group/", key = GroupModel.NAME, code = "159")
public class GroupCtrl {
    @Inject
    private Request request;
    @Inject
    private GroupService groupService;

    @Execute(name = "gets", permit = Permit.always, validates = { @Validate(validator = Validators.SIGN) })
    public Object gets() {
        return groupService.gets();
    }

    @Execute(name = "get", permit = "0", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_SIGN),
            @Validate(validator = GroupService.VALIDATOR_EXISTS, parameter = "id", failureCode = 2),
            @Validate(validator = MemberService.VALIDATOR_IN_GROUP, parameter = "id", failureCode = 104) })
    public Object get() {
        return groupService.get(request.get("id"));
    }

    @Execute(name = "friends", permit = "0", validates = { @Validate(validator = UserService.VALIDATOR_SIGN) })
    public Object friends() {
        return groupService.friends();
    }

    @Execute(name = "find", permit = "0", validates = { @Validate(validator = UserService.VALIDATOR_SIGN) })
    public Object find() {
        return groupService.find(request.get("idUidCode"));
    }
}