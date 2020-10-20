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
     * 获取资源文件内容。。
     *
     * @param name 资源文件名。
     * @return 获取资源文件内容。。
     */
    String resource(String name);
}
