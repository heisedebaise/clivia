package org.lpw.clivia.aliyun;

import java.io.InputStream;

public interface AliyunImage {
    boolean add(String key, String group, String id, String uri);

    String search(String key, String group, String uri, float score);

    String search(String key, String group, InputStream inputStream, float score);

    boolean delete(String key, String id);
}
