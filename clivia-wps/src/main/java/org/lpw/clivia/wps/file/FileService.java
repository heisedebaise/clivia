package org.lpw.clivia.wps.file;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.wps.WpsModel;

import java.util.Map;

/**
 * @author lpw
 */
public interface FileService {
    String preview(WpsModel wps, String uri, String name, int permission, String creator, long create);

    void delete(String wps);

    JSONObject info(String id, Map<String, String> map);

    JSONObject user(String id, Map<String, String> map, String body);
}
