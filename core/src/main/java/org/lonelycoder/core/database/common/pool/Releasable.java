package org.lonelycoder.core.database.common.pool;

/**
 * @author : lihaoquan
 *
 * 可释放接口
 */
public interface Releasable {

    /**
     *  收缩，关闭多余的连接。具体实现取决于实现内部。
     *  这个方法是让连接池尽量少占数据库资源
     */
    void releaseTillMinSize();


    /**
     * 获取对象池状态
     * @return
     */
    PoolStatus getStatus();
}
