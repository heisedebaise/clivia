package org.lpw.clivia.aliyun;

import java.util.Map;

public interface AliyunListener {
    Map<String, String> sync(String key);
}
