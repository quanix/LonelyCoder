package org.lonelycoder.core.database.common.pool;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author : lihaoquan
 *
 * 对注册进来的池进行定期释放的线程
 */
public class ReleaseThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger(ReleaseThread.class);

    private static ReleaseThread prt;

    private static final Map<Long,ReleaseThread> threads = Maps.newHashMap();

    private boolean alive = true;

    private long sleep;

    private final ConcurrentLinkedQueue<Releasable> pools = new ConcurrentLinkedQueue<Releasable>();


    ReleaseThread(long interval) {
        super();
        setDaemon(true);
        setName("Pool Release Thread");
        this.sleep = interval;
    }

    public void addPool(Releasable releasablePool) {
        pools.add(releasablePool);
        if(!isAlive() && alive) {
            start();
        }
    }

    public void removePool(Releasable releasablePool) {
        logPoolStatic(releasablePool);
        pools.remove(releasablePool);
    }


    /**
     * 获取默认单例
     * @return
     */
    public static ReleaseThread getInstance() {
        if(prt == null) {
           return getInstance(3000);
        }
        return prt;
    }

    /**
     * 获取单例
     * @param interval
     * @return
     */
    public static ReleaseThread getInstance(long interval) {
        ReleaseThread releaseThread = threads.get(interval);
        if(releaseThread == null) {
            releaseThread = create(interval);
        }
        return releaseThread;
    }

    /**
     * 同步创建对象
     * @param interval
     * @return
     */
    private synchronized static ReleaseThread create(long interval) {
        ReleaseThread releaseThread = threads.get(interval);
        if(releaseThread == null)  {
            releaseThread = new ReleaseThread(interval);
            if(prt == null) {
                prt = releaseThread;
            }
        }
        return releaseThread;
    }


    static void logPoolStatic(Releasable re){
        PoolStatus status=re.getStatus();
        logger.info("The pool {} poll-count:{} offer-count:{}",re,status.getBorrowCount(),status.getGivebackCount());
    }


    /**
     * 关闭方法
     */
    public void close() {
        this.alive = false;
    }


    private void work() {
        for(Releasable pool : pools) {
            try {
                pool.releaseTillMinSize();
            }catch (Exception e) {
                logger.error("release connecton pool {} error", pool, e);
            }
        }
    }

    @Override
    public void run() {
        while (alive) {
            work();
            try{
                Thread.sleep(sleep);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
