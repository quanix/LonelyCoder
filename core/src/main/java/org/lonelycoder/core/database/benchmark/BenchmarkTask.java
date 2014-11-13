package org.lonelycoder.core.database.benchmark;

import org.lonelycoder.core.util.ThreadUtils;

/**
 * Author : lihaoquan
 * Description :
 */
public abstract class BenchmarkTask implements Runnable {

    /**
     * 持有ConcurrentBenchmark的子类引用
     * 因为子类会实现createTask()的方法
     */
    ConcurrentBenchmark parent;

    @Override
    public void run() {
        setup();
        onThreadStart();
        try {
            for(int i=0 ; i< parent.loopCount; i++) {
                ThreadUtils.await(parent.latchLock);
                taskExecute(i);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            teardown();
            onThreadFinish();
        }

    }


    abstract protected void taskExecute(final int requestSequence);

    protected void onThreadStart(){
        parent.startLock.countDown();
        try {

            ThreadUtils.await(parent.startLock);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onThreadFinish() {
        parent.finishLock.countDown();
    }

    protected void setup() {}

    protected void teardown() {}

    protected void printProgressInfo(final int currentRequests) {
        System.out.println("Gbase-benchmark执行进度:"+currentRequests);
    }
}
