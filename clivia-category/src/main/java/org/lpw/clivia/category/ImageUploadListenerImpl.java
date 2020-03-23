package org.lpw.clivia.category;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Image;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CategoryModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl implements UploadListener {
    @Inject
    private Image image;

    @Override
    public String getKey() {
        return CategoryModel.NAME + ".image";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return image.is(uploadReader.getContentType(), uploadReader.getFileName());
    }
}
