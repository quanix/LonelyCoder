package org.lonelycoder.core.database.common.pool;

/**
 * @author : lihaoquan
 *
 * 对象池
 */
public interface ObjectPool<T> {


    /**
     * 从对象池中借用对象
     * @return
     */
    public T borrow();


    /**
     * 归还池中对象
     * @param obj
     */
    public void giveBack(T obj);


    /**
     * 关闭池,释放全部资源
     */
    public void closePool();
}
