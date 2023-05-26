package org.lpw.clivia.aliyun;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AliyunImage {
    boolean add(String key, String id, String name, String group, String uri);

    boolean add(String key, String id, String group, Map<String, String> map);

    String search(String key, String group, String uri, float score);

    String search(String key, String group, InputStream inputStream, float score);

    boolean delete(String key, String id, String name);

    boolean delete(String key, String id, Set<String> names);
}
