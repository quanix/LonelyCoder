package org.lonelycoder.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Author : lihaoquan
 * Description : 用于并发编程得的若干简单小工具
 */
public abstract class ThreadUtils {

    private static Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    /**
     * 让出指定对象的锁，并且挂起当前线程。只有当—— <li>1. 有别的线程notify了对象，并且锁没有被其他线程占用。</li> <li>2
     * 有别的线程interrupt了当前线程。</li> 此方法才会返回。
     * @param obj 锁所在的对象
     * @return 等待正常结束返回true，异常结束返回false
     */
    public static final boolean doWait(Object obj) {

        synchronized (obj) {
            try {
                obj.wait();
                return true;
            }catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    /**
     * 调用对象的wait方法，并设置超时时间
     * @param obj 锁所在的对象
     * @param timeout 超时时间，单位毫秒
     * @return  超时或正常等待完成返回true。异常退出返回false。
     */
    public static final boolean doWait(Object obj, long timeout) {
        synchronized (obj) {
            try {
                obj.wait(timeout);
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }
    }


    /**
     * 在新的线程中运行指定的任务
     * @param runnable Runnable
     * @return
     */
    public static final Thread doTask(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * 唤醒一个在等待obj锁的线程
     */
    public static final void doNotify(Object obj) {
        synchronized (obj) {
            obj.notify();
        }
    }

    /**
     * 唤醒所有在等待obj的锁的线程。
     *
     * @param obj
     */
    public static final void doNotifyAll(Object obj) {
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    /**
     * 当前线程等待若干毫秒
     *
     * @param l
     *            毫秒数
     * @return 如果是正常休眠后返回的true，因为InterruptedException被打断的返回false
     */
    public static final boolean doSleep(long l) {
        if (l <= 0)
            return true;
        try {
            Thread.sleep(l);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 判断当前该对象是否已锁。<br> 注意在并发场景下，这一操作只能反映瞬时的状态，仅用于检测，并不能认为本次检测该锁空闲，紧接着的代码就能得到锁。
     *
     * @param obj
     * @return
     */
    public static boolean isLocked(Object obj) {
        sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
        if(unsafe.tryMonitorEnter(obj)) {
            unsafe.monitorExit(obj);
            return false;
        }
        return true;
    }


    /**
     * 在执行一个同步方法前，可以手工得到锁。<p>
     *
     * 这个方法可以让你在进入同步方法或同步块之前多一个选择的机会。因为这个方法不会阻塞，如果锁无法得到，会返回false。
     * 如果返回true，证明你可以无阻塞的进入后面的同步方法或同步块。<p>
     *
     * 要注意，用这个方法得到的锁不会自动释放（比如在同步块执行完毕后不会释放），必须通过调用unlock(Object)方法才能释放。 需小心使用。<p>
     *
     * @param obj
     * @return 如果锁得到了，返回true，如果锁没有得到到返回false
     */
    @SuppressWarnings("restriction")
    public static boolean tryLock(Object obj) {
        sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
        return unsafe.tryMonitorEnter(obj);
    }

    /**
     * 释放因为lock/tryLock方法得到的锁
     *
     * @param obj
     */
    @SuppressWarnings("restriction")
    public static void unlock(Object obj) {
        sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
        unsafe.monitorExit(obj);
    }

    /**
     * 对CountDownLatch进行等待。如果正常退出返回true，异常退出返回false
     * @param cl CountDownLatch
     * @return 果正常退出返回true，异常退出返回false
     */
    public static boolean await(CountDownLatch cl) {
        try {
            cl.await();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }


    /**
     * 对CountDownLatch进行等待。如果正常退出返回true，超时或者异常退出返回false
     * @param cl CountDownLatch
     * @param millseconds 超时时间，单位毫秒
     * @return 如果正常退出true。 如果超时或异常退出false
     */
    public static boolean await(CountDownLatch cl,long millseconds) {
        try {
            return cl.await(millseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static void countDown(CountDownLatch cl) {
        try{
            cl.countDown();
        }catch (Exception e) {
        }

    }
}
