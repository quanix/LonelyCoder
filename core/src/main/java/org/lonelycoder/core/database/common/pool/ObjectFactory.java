package org.lonelycoder.core.database.common.pool;

/**
 * @author : lihaoquan
 *
 * 对象池工场
 */
public interface ObjectFactory<T> {

    /**
     * 检查对象是否有效,如果无效的话,则丢弃并创建一个新对象代替原来的对象
     * @param obj
     * @return
     */
    T ensureOpen(T obj);


    /**
     * 释放对象
     * @param obj
     */
    void release(T obj);


    /**
     * 创建对象
     * @return
     */
    T create();
}
