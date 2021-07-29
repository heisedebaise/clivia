package org.lpw.clivia.group.member;

import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

@Controller(MemberModel.NAME + ".ctrl")
@Execute(name = "/group/member/", key = MemberModel.NAME, code = "159")
public class MemberCtrl {
}