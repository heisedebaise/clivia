package org.lpw.clivia.api;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ApiModel.NAME + ".ctrl")
@Execute(name = "/api/", key = ApiModel.NAME, code = "192")
public class ApiCtrl {
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private ApiService apiService;

    @Execute(name = "get", permit = Permit.always)
    public Object get() {
        return apiService.get();
    }
}
