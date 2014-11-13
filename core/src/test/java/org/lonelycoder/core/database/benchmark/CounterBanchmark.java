package org.lonelycoder.core.database.benchmark;

/**
 * Author : lihaoquan
 * Description :
 */
public class CounterBanchmark extends ConcurrentBenchmark {


    private static final int DEFAULT_THREAD_COUNT = 200; //模拟n个客户端
    private static final int DEFAULT_LOOP_COUNT = 3;//模拟每个客户端单位时刻,请求查询次(例如,一个客户端的首页可能同时查执行n个语句查询)

    /**
     * 构造函数
     *
     */
    public CounterBanchmark() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT);
    }

    @Override
    protected BenchmarkTask createTask() {
        return new BenchmarkTask() {
            @Override
            protected void taskExecute(int requestSequence) {
                System.out.println("CounterTask:execute:"+requestSequence);
                System.out.println("================================");
            }
        };
    }

    public static void main(String[] args) throws Exception {
        CounterBanchmark benchmark = new CounterBanchmark();
        benchmark.execute();
    }
}
