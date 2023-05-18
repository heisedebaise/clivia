package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.storage.Storages;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;

@Controller(WeixinModel.NAME + ".upload-listener.mp-verify")
public class MpVerifyUploadListenerImpl implements UploadListener {
    @Inject
    private Storages storages;

    @Override
    public String getKey() {
        return WeixinModel.NAME + ".mp-verify";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return uploadReader.getContentType().equals("text/plain")
                && uploadReader.getFileName().startsWith("MP_verify_") && uploadReader.getFileName().endsWith(".txt");
    }

    @Override
    public JSONObject settle(UploadReader uploadReader) throws IOException {
        String path = "/" + uploadReader.getFileName();
        uploadReader.write(storages.get(Storages.TYPE_DISK), path);
        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("path", path);
        object.put("name", getKey());
        object.put("fileName", uploadReader.getFileName());
        object.put("fileSize", uploadReader.getSize());

        return object;
    }
}
