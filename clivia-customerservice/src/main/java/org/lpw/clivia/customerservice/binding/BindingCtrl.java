package org.lpw.clivia.customerservice.binding;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(BindingModel.NAME + ".ctrl")
@Execute(name = "/customerservice/binding/", key = BindingModel.NAME, code = "158")
public class BindingCtrl {
    @Inject
    private Request request;
    @Inject
    private BindingService bindingService;
}
