package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Numeric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service(ConsoleModel.NAME + ".service")
public class ConsoleServiceImpl implements ConsoleService, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private UserService userService;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;
    private JSONObject dashboard;

    @Override
    public JSONArray dashboard() {
        String grade = numeric.toString(userService.grade(), "0");

        return json.containsKey(dashboard, grade) ? dashboard.getJSONArray(grade) : new JSONArray();
    }

    @Override
    public int getContextRefreshedSort() {
        return 191;
    }

    @Override
    public void onContextRefreshed() {
        dashboard = json.toObject(io.readAsString(context.getAbsolutePath(console + "dashboard.json")), false);
    }
}
