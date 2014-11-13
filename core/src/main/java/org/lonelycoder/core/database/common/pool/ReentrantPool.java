package org.lonelycoder.core.database.common.pool;

import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : lihaoquan
 */
public class ReentrantPool<T> extends SimplePool<T> {

    final Map<Object,Wrapper<T>> map = new MapMaker().concurrencyLevel(12).weakKeys().makeMap();
    private final AtomicLong borrowCount = new AtomicLong();
    private final AtomicLong givebackCount = new AtomicLong();

    /**
     * 构造函数
     *
     * @param factory
     * @param minSize
     * @param maxSize
     */
    public ReentrantPool(ObjectFactory<T> factory, int minSize, int maxSize) {
        super(factory, minSize, maxSize);
    }


    @Override
    public T borrow() {
        borrowCount.incrementAndGet();
        Thread userthread = Thread.currentThread();
        Wrapper<T> conn = map.get(userthread);
        if(conn == null) {
            conn = new Wrapper<T>(super.borrow());
            map.put(userthread,conn);
            conn.setUserdByObject(userthread);
        }else {
            conn.addUsedByObject();
        }
        return conn.obj;
    }

    @Override
    public void giveBack(T o) {
        givebackCount.incrementAndGet();
        Thread userthread = Thread.currentThread();
        Wrapper<T> conn = map.get(userthread);
        if(conn == null || conn.obj != o) {//不是在同一个线程中归还
            conn = null;
            for(Wrapper<T> w : map.values()) {
                if(w.obj == o) {
                    conn = w;
                }
            }
            if(conn == null) {
                throw new IllegalStateException("The object "+o+" is unknown in reentrant map.");
            }
        }

        //
        Object u = conn.popUsedObject();
        if (u == null)
            return;// 不是真正的归还
        Wrapper<T> conn1 = map.remove(u);
        if (conn1 != conn) {
            throw new IllegalStateException("The connection returned not match.");
        }
        super.giveBack(conn1.obj);
    }

    @Override
    public void closePool() {
        super.closePool();
        for(Wrapper<T> wrapper : map.values()) {
            T obj = wrapper.obj;
            if(obj!=null) {
                factory.release(obj);
            }
        }
    }


    @Override
    public PoolStatus getStatus() {
        PoolStatus st = super.getStatus();
        st.setBorrowCount(borrowCount.get());
        st.setGivebackCount(givebackCount.get());
        return st;
    }

    /**
     * 封装类
     * @param <T>
     */
    private static class Wrapper<T> {

        private T obj;
        private volatile Object usedObj;
        private volatile int usedCount;

        public Wrapper(T create) {
            this.obj = create;
        }

        void setUserdByObject(Object object) {
            this.usedObj = object;
            usedCount ++;
        }

        Object popUsedObject() {
            if(--usedCount > 0) {
                return null;
            }else {
                Object o = usedObj;
                usedObj = null;
                return o;
            }
        }

        void addUsedByObject() {
            usedCount++;
        }

    }
}
