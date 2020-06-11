package org.lpw.clivia.wps;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(WpsModel.NAME + ".ctrl")
@Execute(name = "/wps/", key = WpsModel.NAME, code = "157")
public class WpsCtrl {
    @Inject
    private Request request;
    @Inject
    private WpsService wpsService;
}
