package org.lonelycoder.core.database.common.pool;

import org.lonelycoder.core.util.Assert;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : lihaoquan
 *
 * 一个简单的对象池实现
 */
public class SimplePool<T> implements ObjectPool<T>,Releasable {


    private int maxSize;
    private int minSize;

    private BlockingQueue<T> freeConnections;
    private AtomicInteger usedCount = new AtomicInteger();//被接走的连接数
    protected ObjectFactory<T> factory;

    /**
     * 构造函数
     * @param factory
     * @param minSize
     * @param maxSize
     */
    public SimplePool(ObjectFactory<T> factory,int minSize, int maxSize) {
        if(minSize > maxSize)
            minSize = maxSize;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.factory = factory;
        Assert.notNull(factory);
        freeConnections = new LinkedBlockingQueue<T>(maxSize);
        ReleaseThread.getInstance().addPool(this);
    }


    /**
     * 从连接池中借连接
     * @return
     */
    @Override
    public T borrow() {
        try {
            T obj;
            if(freeConnections.isEmpty() && usedCount.get() < maxSize) {
                usedCount.incrementAndGet();
                obj = factory.create();
            }else {
                obj = freeConnections.poll(5000000000L, TimeUnit.NANOSECONDS);// 5秒
                if(obj == null) {
                    throw new IllegalStateException("No object avaliable now.");
                }
                usedCount.incrementAndGet();
                obj = factory.ensureOpen(obj);
            }
            return obj;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void giveBack(T obj) {

        boolean success = freeConnections.offer(obj);
        usedCount.decrementAndGet();
        if(!success) {
            factory.release(obj);
        }
    }

    @Override
    public void closePool() {
        ReleaseThread.getInstance().removePool(this);
        maxSize = 0;
        minSize = 0;
        releaseTillMinSize();
    }


    /**
     * 对象池收缩
     */
    @Override
    public void releaseTillMinSize() {
        if (freeConnections.size() > minSize) {
            T obj;
            while ( (obj = freeConnections.poll()) !=null && freeConnections.size()> minSize ) {
                try {
                    factory.release(obj);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 连接池状态
     * @return
     */
    @Override
    public PoolStatus getStatus() {
        int usedCount = this.usedCount.get();
        int freeCount = this.freeConnections.size();
        return new PoolStatus(maxSize,minSize,usedCount+freeCount,usedCount,freeCount);
    }
}
