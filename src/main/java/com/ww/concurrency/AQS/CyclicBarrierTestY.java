package com.ww.concurrency.AQS;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/*CountDownLatch和CyclicBarrier区别：
        区别1：
        CountDownLatch的计数器只能使用1次
        CyclicBarrier的计数器可以使用reset方法重置，循环使用
        区别2：
        CountDownLatch用于一个线程或多个线程一直等待，直到某个线程执行的操作完成
        CyclicBarrier用于多个线程之间相互等待，直到所有线程都满足条件，才能继续执行后续的操作*/
@Slf4j
public class CyclicBarrierTestY {

    private final static int REQ_TOTAL = 10;

    //假如5个线程等待都OK再继续向下执行
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5,()->{
        log.info("after ready first run!");//5个线程都await好之后执行这里
    });

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();


        for (int i = 0; i < REQ_TOTAL; i++) {
            final int threadNum = i;
            executorService.execute(() -> {
                try {
                    simuService(threadNum);
                } catch (Exception e) {
                    log.error("【Exception】:{}", e);
                }
            });
        }
        //当前正在运行的线程运行完才结束，不是直接全部kill
        executorService.shutdown();
        log.info("Finished!");
    }


    private static void simuService(int threadNum) throws Exception {
        Thread.sleep(1000);
        log.info("【ThreadNum】：{} is Ready!", threadNum);
        //每个线程都执行await方法，告知自己OK了
        try {
            cyclicBarrier.await(2000,TimeUnit.MILLISECONDS);
        }catch (InterruptedException| BrokenBarrierException | TimeoutException e){
            log.warn("【BarrierException】",e);
        }

        //await的线程数量达到cyclicBarrier的数量，这些线程继续向后执行
        log.info("【ThreadNum】：{} continued!", threadNum);
    }
}
