package org.lpw.clivia.user.stat;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(StatModel.NAME + ".ctrl")
@Execute(name = "/user/stat/", key = StatModel.NAME, code = "151")
public class StatCtrl {
    @Inject
    private Request request;
    @Inject
    private StatService statService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return statService.query(request.get("date"));
    }

    @Execute(name = "today", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object today(){
        return statService.today();
    }
}
