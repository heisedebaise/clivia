package org.lpw.clivia.user.inviter;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(InviterModel.NAME + ".ctrl")
@Execute(name = "/user/inviter/", key = InviterModel.NAME, code = "151")
public class InviterCtrl {
    @Inject
    private Request request;
    @Inject
    private InviterService inviterService;
}
