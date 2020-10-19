package org.lpw.clivia.api;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(ApiModel.NAME + ".ctrl")
@Execute(name = "/api/", key = ApiModel.NAME, code = "192")
public class ApiCtrl {
    @Inject
    private Request request;
    @Inject
    private ApiService apiService;

    @Execute(name = "get")
    public Object get() {
        return apiService.get();
    }

    @Execute(name = "request-demo")
    public Object requestDemo() {
        return apiService.js("request-demo");
    }
}
