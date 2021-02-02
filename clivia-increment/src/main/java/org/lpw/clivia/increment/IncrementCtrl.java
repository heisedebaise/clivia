package org.lpw.clivia.increment;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(IncrementModel.NAME + ".ctrl")
@Execute(name = "/increment/", key = IncrementModel.NAME, code = "0")
public class IncrementCtrl {
    @Inject
    private Request request;
    @Inject
    private IncrementService incrementService;
}
