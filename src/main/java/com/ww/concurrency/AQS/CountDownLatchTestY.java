package com.ww.concurrency.AQS;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class CountDownLatchTestY {

    private final static int requestTotal = 200;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(requestTotal);

        for (int i = 0; i < requestTotal; i++) {
            final int threadNum = i;
            executorService.execute(() -> {
                try {
                    simuService(threadNum);
                } catch (Exception e) {
                    log.error("【Exception】:{}", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            //总共只等待10毫秒就继续向下执行了
            countDownLatch.await(10, TimeUnit.MILLISECONDS);
            log.info("Finished!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //当前正在运行的线程运行完才结束，不是直接全部kill
            executorService.shutdown();
        }
    }


    private static void simuService(int threadNum) throws Exception {

        Thread.sleep(100);
        log.info("【ThreadNum】：{}", threadNum);
    }
}
