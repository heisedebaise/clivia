package org.lpw.clivia.wps;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Image;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WpsModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl implements UploadListener {
    @Inject
    private Image image;

    @Override
    public String getKey() {
        return WpsModel.NAME + ".image";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return image.is(uploadReader.getContentType(), uploadReader.getFileName());
    }
}
