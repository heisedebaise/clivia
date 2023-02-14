package org.lpw.clivia.olcs.member;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MemberModel.NAME + ".ctrl")
@Execute(name = "/olcs/member/", key = MemberModel.NAME, code = "162")
public class MemberCtrl {
    @Inject
    private Request request;
    @Inject
    private MemberService memberService;

    @Execute(name = "query", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object query() {
        return memberService.query();
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return memberService.user();
    }

    @Execute(name = "unread", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object unread() {
        return memberService.unread();
    }
}