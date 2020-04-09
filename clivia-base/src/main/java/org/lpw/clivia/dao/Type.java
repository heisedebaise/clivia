package org.lpw.clivia.dao;

/**
 * @author lpw
 */
public interface Type {
    /**
     * 获取类型。
     *
     * @return 类型。
     */
    ColumnType getType();

    /**
     * 判断是否为空。
     *
     * @param object 对象。
     * @return 为空则返回true；否则返回false。
     */
    boolean isEmpty(Object object);
}
