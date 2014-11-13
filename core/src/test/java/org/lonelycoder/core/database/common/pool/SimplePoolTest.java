package org.lonelycoder.core.database.common.pool;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : lihaoquan
 */
public class SimplePoolTest {

    public static void main(String[] args) {

        SimplePool<String> pool = new SimplePool<String>(new ObjectFactory<String>() {
            @Override
            public String ensureOpen(String obj) {
                return obj;
            }

            @Override
            public void release(String obj) {

            }

            @Override
            public String create() {
                return RandomStringUtils.random(8); //产生8位的随机数字
            }
        },1,4);

        int threadCount = 10;

        for(int i= 0; i< threadCount; i++) {
            MyThread t1 = new MyThread(pool);
            t1.start();
        }

        System.out.println("Finished");
        pool.closePool();
    }


    static class MyThread extends Thread {

        SimplePool<String> pool;

        public MyThread(SimplePool simplePool) {
            this.pool = simplePool;
        }

        @Override
        public void run() {
            for(int i = 0; i<100; i++) {
                String s = pool.borrow();
                pool.giveBack(s);
                System.out.println(pool.getStatus());
            }
        }
    }
}
