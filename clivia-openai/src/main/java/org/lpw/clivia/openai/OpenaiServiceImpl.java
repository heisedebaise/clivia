package org.lpw.clivia.openai;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.upload.UploadService;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

@Service(OpenaiModel.NAME + ".service")
public class OpenaiServiceImpl implements OpenaiService {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private Logger logger;
    @Inject
    private UploadService uploadService;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private OpenaiDao openaiDao;

    @Override
    public JSONObject query(String key) {
        return openaiDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public boolean has(String key) {
        return openaiDao.findByKey(key) != null;
    }

    @Override
    public void save(OpenaiModel openai) {
        OpenaiModel model = validator.isId(openai.getId()) ? openaiDao.findById(openai.getId()) : null;
        if (model == null)
            openai.setId(null);
        openaiDao.save(openai);
    }

    @Override
    public void delete(String id) {
        openaiDao.delete(id);
    }

    @Override
    public String chat(String key, String user, String content) {
        OpenaiModel openai = openaiDao.findByKey(key);
        if (openai == null)
            return null;

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("name", aiUser(user));
        message.put("content", content);
        JSONArray messages = new JSONArray();
        messages.add(message);
        JSONObject parameter = new JSONObject();
        parameter.put("model", openai.getChat());
        parameter.put("messages", messages);
        String string = http.post("https://api.openai.com/v1/chat/completions", header(openai), json.toString(parameter));
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("choices")) {
            logger.warn(null, "调用OpenAI聊天[{}:{}]失败！", parameter, string);

            return null;
        }

        StringBuilder sb = new StringBuilder();
        JSONArray choices = object.getJSONArray("choices");
        for (int i = 0, size = choices.size(); i < size; i++) {
            JSONObject choice = choices.getJSONObject(i);
            if (choice.containsKey("message")) {
                JSONObject msg = choice.getJSONObject("message");
                if (msg.containsKey("content"))
                    sb.append(msg.getString("content"));
            }
        }

        return sb.toString().trim();
    }

    @Override
    public String image(String key, String user, String content, int count) {
        OpenaiModel openai = openaiDao.findByKey(key);
        if (openai == null)
            return null;

        JSONObject parameter = new JSONObject();
        parameter.put("prompt", content);
        parameter.put("n", count);
        parameter.put("size", "1024x1024");
        parameter.put("response_format", "url");
        parameter.put("user", aiUser(user));
        String string = http.post("https://api.openai.com/v1/images/generations", header(openai), json.toString(parameter));
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("data")) {
            logger.warn(null, "调用OpenAI绘图[{}:{}]失败！", parameter, string);

            return null;
        }


        StringBuilder sb = new StringBuilder();
        JSONArray data = object.getJSONArray("data");
        for (int i = 0, size = data.size(); i < size; i++) {
            JSONObject d = data.getJSONObject(i);
            if (d.containsKey("url")) {
                String uri = download(d.getString("url"));
                if (uri != null)
                    sb.append('\n').append(uri);
            }
        }

        return sb.toString().trim();
    }

    private String aiUser(String user) {
        return user == null ? "" : user.replaceAll("-", "");
    }

    private Map<String, String> header(OpenaiModel openai) {
        return Map.of("Content-Type", "application/json",
                "Authorization", "Bearer " + openai.getAuthorization(),
                "OpenAI-Organization", openai.getOrganization()
        );
    }

    private String download(String url) {
        String suffix = url.substring(url.lastIndexOf('.'), url.indexOf('?'));
        String uri = uploadService.newSavePath("image/" + suffix.substring(1), "", suffix);
        try (OutputStream outputStream = new FileOutputStream(context.getAbsolutePath(uri))) {
            http.get(url, null, null, null, outputStream);

            return uri;
        } catch (Throwable throwable) {
            logger.warn(throwable, "下载AI绘图[{}]失败！", url);

            return null;
        }
    }
}
