package org.lonelycoder.core.database.benchmark;

import org.lonelycoder.core.util.ThreadUtils;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author : lihaoquan
 * Description : 基准测试抽象类
 */
public abstract class ConcurrentBenchmark {

    private static final String THREAD_COUNT_NAME = "benchmark.thread.count";
    private static final String LOOP_COUNT_NAME= "benchmark.loop.count";

    public int threadCount;
    public long loopCount;

    public CountDownLatch startLock;
    public CountDownLatch finishLock;

    public CountDownLatch latchLock = new CountDownLatch(1);

    public Date startTime;
    public int intervalMillis = 10 * 1000;


    /**
     * 构造函数
     * @param defaultThreadCount
     * @param defaultLoopCount
     */
    public ConcurrentBenchmark(int defaultThreadCount,int defaultLoopCount) {

        this.threadCount = Integer.parseInt(System.getProperty(THREAD_COUNT_NAME,String.valueOf(defaultThreadCount)));
        this.loopCount = Long.parseLong(System.getProperty(LOOP_COUNT_NAME,String.valueOf(defaultLoopCount)));

        startLock = new CountDownLatch(threadCount);
        finishLock = new CountDownLatch(threadCount);
    }


    /**
     * 执行方法
     */
    public void execute() throws Exception {

        setup();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        try {

            for(int i = 0; i< threadCount; i++) {
                BenchmarkTask benchmarkTask = createTask();
                benchmarkTask.parent = this;
                executorService.execute(benchmarkTask);
            }

            ThreadUtils.await(startLock);
            printStartMessage();

            latchLock.countDown();

            ThreadUtils.await(finishLock);
            printFinishMessage();

        }finally {
            executorService.shutdownNow();
            tearDown();
        }
    }

    protected void setup() {

    }

    protected void tearDown() {

    }

    protected abstract BenchmarkTask createTask();

    protected void setIntervalMillis(int intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    protected void printStartMessage() {
        System.out.println("基准测试开始");
    }

    protected void printFinishMessage() {
        System.out.println("基准测试结束");
    }

}
