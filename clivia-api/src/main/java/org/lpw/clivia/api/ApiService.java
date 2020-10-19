package org.lpw.clivia.api;

import com.alibaba.fastjson.JSONArray;

/**
 * @author lpw
 */
public interface ApiService {
    /**
     * 获取API配置集。
     *
     * @return API配置集。
     */
    JSONArray get();

    /**
     * 获取JS资源。
     *
     * @param name 资源名。
     * @return JS资源。
     */
    String js(String name);
}
