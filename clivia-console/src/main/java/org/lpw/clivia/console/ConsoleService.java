package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;

/**
 * @author lpw
 */
public interface ConsoleService {
    /**
     * 获取仪表盘配置。
     *
     * @return 仪表盘配置。
     */
    JSONArray dashboard();
}
