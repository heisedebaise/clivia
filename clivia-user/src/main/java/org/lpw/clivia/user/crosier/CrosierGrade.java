package org.lpw.clivia.user.crosier;

import com.alibaba.fastjson.JSONArray;

/**
 * 角色等级。
 *
 * @author lpw
 */
public interface CrosierGrade {
    /**
     * 获取注册等级集。
     *
     * @return 注册等级集。
     */
    JSONArray signUpGrades();

    /**
     * 获取注册等级。
     *
     * @param grade 目标等级。
     * @return 等级值。
     */
    int signUpGrade(String grade);

    /**
     * 获取等级集。
     *
     * @return 等级集。
     */
    JSONArray grades();
}
