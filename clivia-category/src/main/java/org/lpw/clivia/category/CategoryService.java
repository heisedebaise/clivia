package org.lpw.clivia.category;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface CategoryService {
    String LEAF_VALIDATOR = CategoryModel.NAME + ".validator.leaf";

    JSONArray query(String key);

    JSONObject find(String id);

    void save(CategoryModel category);

    void delete(String id);
}
