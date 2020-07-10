package org.lpw.clivia.wps.file;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(FileModel.NAME + ".ctrl")
@Execute(name = "/wps/file/", key = FileModel.NAME, code = "0")
public class FileCtrl {
    @Inject
    private Request request;
    @Inject
    private FileService fileService;
}
