package org.lpw.clivia.mindmap;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MindmapModel.NAME + ".ctrl")
@Execute(name = "/mindmap/", key = MindmapModel.NAME, code = "117")
public class MindmapCtrl {
    @Inject
    private Request request;
    @Inject
    private MindmapService mindmapService;
}