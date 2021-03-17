package org.lpw.clivia.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.freemarker.Freemarker;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service(ModuleModel.NAME + ".service")
public class ModuleServiceImpl implements ModuleService {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Freemarker freemarker;
    @Inject
    private Logger logger;
    @Inject
    private Json json;
    @Inject
    private Pagination pagination;
    @Inject
    private ModuleDao moduleDao;
    @Value("${" + ModuleModel.NAME + ".package:org.lpw.clivia}")
    private String pkg;
    @Value("${" + ModuleModel.NAME + ".version:1.0}")
    private String version;
    @Value("${" + ModuleModel.NAME + ".url:https://github.com/heisedebaise/clivia}")
    private String url;
    @Value("${" + ModuleModel.NAME + ".output:/WEB-INF/module/}")
    private String output;

    @Override
    public JSONObject query() {
        return moduleDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(ModuleModel module) {
        ModuleModel model = validator.isId(module.getId()) ? moduleDao.findById(module.getId()) : null;
        if (model == null)
            module.setId(null);
        moduleDao.save(module);
    }

    @Override
    public void generate(String id) {
        ModuleModel module = moduleDao.findById(id);
        String project = pkg.substring(pkg.lastIndexOf('.') + 1);
        String name = project + "-" + module.getName();
        String path = context.getAbsolutePath(output + name) + "/";
        io.delete(path);
        String java = path + "src/main/java/" + pkg.replace('.', '/') + "/" + main(module, "/") + module.getName() + "/";
        io.mkdirs(java);
        String resources = path + "src/main/resources/" + pkg.replace('.', '/') + "/" + module.getName() + "/";
        io.mkdirs(resources);
        JSONArray columns = json.toArray(module.getColumns());
        String upper = upper(module.getName());
        try {
            if (validator.isEmpty(module.getMain()))
                pom(name, path);
            model(module, upper, columns, java);
            create(module, columns, resources);
            message(project, module, columns, resources);
            meta(project, module, columns, resources);
        } catch (Throwable throwable) {
            logger.warn(throwable, "生成模块[{}]时发生异常！", json.toObject(module));
        }
    }

    private String upper(String string) {
        StringBuilder sb = new StringBuilder();
        boolean upper = true;
        for (char ch : string.toCharArray()) {
            if (ch == '_') {
                upper = true;

                continue;
            }

            if (upper) {
                sb.append((char) (ch - 'a' + 'A'));
                upper = false;
            } else
                sb.append(ch);
        }

        return sb.toString();
    }

    private void pom(String name, String path) throws IOException {
        OutputStream outputStream = new FileOutputStream(path + "pom.xml");
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", pkg);
        map.put("name", name);
        map.put("version", version);
        map.put("url", url);
        freemarker.process("/module/pom", map, outputStream);
        outputStream.close();
    }

    private void model(ModuleModel module, String upper, JSONArray columns, String path) throws IOException {
        OutputStream outputStream = new FileOutputStream(path + upper + "Model.java");
        Map<String, Object> map = new HashMap<>();
        map.put("pkg", pkg + "." + main(module, ".") + module.getName());
        map.put("columns", columns);
        freemarker.process("/module/model", map, outputStream);
        outputStream.close();
    }

    private void create(ModuleModel module, JSONArray columns, String path) throws IOException {
        OutputStream outputStream = new FileOutputStream(path + "create.sql");
        Map<String, Object> map = new HashMap<>();
        map.put("name", main(module, "_") + module.getName());
        map.put("columns", columns);
        freemarker.process("/module/create", map, outputStream);
        outputStream.close();
    }

    private void message(String project, ModuleModel module, JSONArray columns, String path) throws IOException {
        OutputStream outputStream = new FileOutputStream(path + "message.properties");
        Map<String, Object> map = new HashMap<>();
        map.put("prefix", project + "." + main(module, ".") + module.getName());
        map.put("columns", columns);
        freemarker.process("/module/message", map, outputStream);
        outputStream.close();
    }

    private void meta(String project, ModuleModel module, JSONArray columns, String path) throws IOException {
        OutputStream outputStream = new FileOutputStream(path + "meta.json");
        Map<String, Object> map = new HashMap<>();
        map.put("key", project + "." + main(module, ".") + module.getName());
        map.put("uri", "/" + main(module, "/") + module.getName() + "/");
        map.put("execute", module.getExecute());
        map.put("columns", columns);
        freemarker.process("/module/meta", map, outputStream);
        outputStream.close();
    }

    private String main(ModuleModel module, String suffix) {
        return validator.isEmpty(module.getMain()) ? "" : (module.getMain() + suffix);
    }

    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }
}
