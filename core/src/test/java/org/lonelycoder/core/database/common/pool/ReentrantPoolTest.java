package org.lonelycoder.core.database.common.pool;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author : lihaoquan
 */
public class ReentrantPoolTest {

    public static void main(String[] args) throws Exception {
        ReentrantPool<String> pool=new ReentrantPool<String>(new ObjectFactory<String>() {
            public String ensureOpen(String conn) {
                return conn;
            }

            public void release(String conn) {
            }

            public String create() {
                return RandomStringUtils.random(8);
            }
        },1,4);

        int threadCount=10;

        CountDownLatch c=new CountDownLatch(threadCount*5);

        for(int i=0;i< threadCount;i++){
            Th t1=new Th(pool,c);
            Th t2=new Th(pool,c);
            Th t3=new Th(pool,c);
            Th t4=new Th(pool,c);
            Th t5=new Th(pool,c);

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
        }
        c.await();
        System.out.println("Finished");
        pool.closePool();
    }

    static class Th extends Thread{
        ReentrantPool<String> pool;
        CountDownLatch c;

        public Th(ReentrantPool<String> pool,CountDownLatch c) {
            this.pool=pool;
            this.c=c;
        }

        @Override
        public void run() {
            for(int i=0;i<300;i++){
                String s=pool.borrow();
                String s2=pool.borrow();
                if(s!=s2)throw new RuntimeException();//在同一个线程内两次获取对象，应该要得到同一个对象。
                pool.giveBack(s);
                pool.giveBack(s2); //释放也要两次才算真正的释放
            }
            c.countDown();
            System.out.println(pool.getStatus());
        }
    }
}
