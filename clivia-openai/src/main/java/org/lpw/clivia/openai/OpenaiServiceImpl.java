package org.lpw.clivia.openai;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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
    private Logger logger;
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
    public String chat(String key, String content) {
        OpenaiModel openai = openaiDao.findByKey(key);
        if (openai == null)
            return null;

        JSONObject message = new JSONObject();
        message.put("role", "user");
        String name = userService.id();
        message.put("name", name == null ? "" : name.replaceAll("-", ""));
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

        return sb.toString();
    }

    @Override
    public String image(String key, String content) {
        OpenaiModel openai = openaiDao.findByKey(key);
        if (openai == null)
            return null;

        JSONObject parameter = new JSONObject();
        parameter.put("prompt", content);
        parameter.put("n", "2");
        parameter.put("size", "1024x1024");
        parameter.put("response_format", "url");
        String name = userService.id();
        parameter.put("user", name == null ? "" : name.replaceAll("-", ""));
        String string = http.post("https://api.openai.com/v1/chat/completions", header(openai), json.toString(parameter));
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("data")) {
            logger.warn(null, "调用OpenAI绘图[{}:{}]失败！", parameter, string);

            return null;
        }


        StringBuilder sb = new StringBuilder();
        JSONArray data = object.getJSONArray("data");
        for (int i = 0, size = data.size(); i < size; i++) {
            JSONObject d = data.getJSONObject(i);
            if (d.containsKey("url"))
                sb.append(',').append(d.getString("url"));
        }

        return sb.length() == 0 ? "" : sb.substring(1);
    }

    private Map<String, String> header(OpenaiModel openai) {
        return Map.of("Content-Type", "application/json",
                "Authorization", "Bearer " + openai.getAuthorization(),
                "OpenAI-Organization", openai.getOrganization()
        );
    }
}
