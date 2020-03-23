package org.lpw.clivia.weixin.media;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Image;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(MediaModel.NAME + ".upload-listener")
public class UploadListenerImpl implements UploadListener {
    @Inject
    private Image image;

    @Override
    public String getKey() {
        return MediaModel.NAME;
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return image.is(uploadReader.getContentType(), uploadReader.getFileName());
    }
}
