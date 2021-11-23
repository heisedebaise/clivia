package org.lpw.clivia.faq;

import com.alibaba.fastjson.JSONObject;

public interface FaqService {
    JSONObject query(String key);

    void save(FaqModel faq);

    void delete(String id);
}
