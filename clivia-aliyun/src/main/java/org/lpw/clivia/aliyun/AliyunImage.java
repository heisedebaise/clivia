package org.lpw.clivia.aliyun;

import java.util.Map;
import java.util.Set;

public interface AliyunImage {
    boolean add(String key, String id, String name, String group, String uri);

    boolean add(String key, String id, String group, Map<String, String> map);

    Set<String> search(String key, String group, String uri);

    boolean delete(String key, String id, String name);

    boolean delete(String key, String id, Set<String> names);
}
