package org.lpw.clivia.user;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Image;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserModel.NAME + ".upload-listener.avatar")
public class AvatarUploadListenerImpl implements UploadListener {
    @Inject
    private Image image;
    @Inject
    private UserService userService;

    @Override
    public String getKey() {
        return UserModel.NAME + ".avatar";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return image.is(uploadReader.getContentType(), uploadReader.getFileName()) && !userService.sign().isEmpty();
    }
}
