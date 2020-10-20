package org.lpw.clivia.api;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
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
    private Templates templates;
    @Inject
    private ApiService apiService;

    @Execute(name = "get")
    public Object get() {
        return apiService.get();
    }

    @Execute(name = "request")
    public Object request() {
        return apiService.resource("request.js");
    }

    @Execute(name = "response")
    public Object response() {
        return templates.get().success(apiService.resource("response.json"), null);
    }
}
