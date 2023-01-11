package org.lpw.clivia.olcs.faq;

import com.alibaba.fastjson.JSONObject;

public interface FaqService {

    JSONObject query();

    JSONObject user();

    void save(FaqModel faq);

    void delete(String id);
}
