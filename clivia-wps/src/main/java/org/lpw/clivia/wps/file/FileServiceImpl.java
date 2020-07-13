package org.lpw.clivia.wps.file;

import org.lpw.clivia.user.UserService;
import org.lpw.clivia.wps.WpsModel;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Service(FileModel.NAME + ".service")
public class FileServiceImpl implements FileService, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Converter converter;
    @Inject
    private Digest digest;
    @Inject
    private Codec codec;
    @Inject
    private Logger logger;
    @Inject
    private UserService userService;
    @Inject
    private FileDao fileDao;
    private final Map<String, String> types = new HashMap<>();

    @Override
    public String preview(WpsModel wps, String uri, String name, int permission, String creator, long create) {
        if (wps == null)
            return null;

        int index = uri.lastIndexOf('.');
        if (index == -1)
            return null;

        String suffix = uri.substring(index + 1);
        if (!types.containsKey(suffix))
            return null;

        File file = new File(context.getAbsolutePath(uri));
        if (!file.exists())
            return null;

        FileModel model = fileDao.find(wps.getId(), uri, permission);
        if (model == null) {
            model = new FileModel();
            model.setWps(wps.getId());
            model.setUri(uri);
            model.setName(name);
            model.setPermission(permission);
            model.setVersion(1);
            model.setSize(file.length());
            model.setCreator(creator);
            model.setCreate(create);
            model.setModifier(creator);
            model.setModify(create);
            fileDao.save(model);
        }

        Map<String, String> map = new HashMap<>();
        map.put("_w_appid", wps.getAppId());
        map.put("_w_user", userService.id());
        map.put("_w_signature", signature(wps, map));
        StringBuilder sb = new StringBuilder("https://wwo.wps.cn/office/").append(types.get(suffix)).append('/').append(model.getId()).append('?');
        map.forEach((key, value) -> sb.append(key).append('=').append(codec.encodeUrl(value, null)).append('&'));

        return sb.substring(0, sb.length() - 1);
    }

    private String signature(WpsModel wps, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        keys.stream().filter(key -> key.startsWith("_w_")).forEach(key -> sb.append(key).append('=').append(map.get(key)));
        sb.append("_w_secretkey=").append(wps.getSecret());

        return codec.encodeBase64(digest.hmacSHA1(wps.getSecret().getBytes(), sb.toString().getBytes()));
    }

    @Override
    public void delete(String wps) {
        fileDao.delete(wps);
    }

    @Override
    public int getContextRefreshedSort() {
        return 157;
    }

    @Override
    public void onContextRefreshed() {
        for (String key : converter.toArray("doc,dot,wps,wpt,docx,dotx,docm,dotm,rtf", ",")) {
            types.put(key, "w");
        }
        for (String key : converter.toArray("xls,xlt,et,xlsx,xltx,csv,xlsm,xltm", ",")) {
            types.put(key, "s");
        }
        for (String key : converter.toArray("ppt,pptx,pptm,ppsx,ppsm,pps,potx,potm,dpt,dps", ",")) {
            types.put(key, "p");
        }
        types.put("pdf", "f");
        logger.info("设置WPS后缀格式：{}。", types);
    }
}
