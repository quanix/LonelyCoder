package org.lonelycoder.core.database.common.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : lihaoquan
 */
public class ConcurrentLinkedQueueTest {

    private static ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();

    private static int thread_count = 2;

    private static CountDownLatch latch = new CountDownLatch(thread_count);

    public static void main(String[] args) throws Exception {
        long timeStart = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        ConcurrentLinkedQueueTest.offer();

        for(int i = 0 ; i< thread_count; i++) {
            executorService.submit(new Poll());
        }
        latch.await();
        System.out.println("cost time :"+(System.currentTimeMillis()-timeStart)+" ms");
        executorService.shutdown();

    }


    /**
     * 生产
     */
    public static void offer() {
        for(int i = 0 ; i< 100000; i++) {
            queue.offer(i);
        }
    }


    /**
     * 消费
     */
    static class Poll implements Runnable {

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                System.out.println(queue.poll());
            }
            latch.countDown();
        }
    }

}
