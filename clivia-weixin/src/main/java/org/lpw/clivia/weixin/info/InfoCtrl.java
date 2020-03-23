package org.lpw.clivia.weixin.info;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(InfoModel.NAME + ".ctrl")
@Execute(name = "/weixin/info/", key = InfoModel.NAME, code = "124")
public class InfoCtrl {
    @Inject
    private Request request;
    @Inject
    private InfoService infoService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object query() {
        return infoService.query();
    }
}
