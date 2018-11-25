package com.ww.concurrency.AQS;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


@Slf4j
public class SemophoreTryTestY {

    private final static int requestCount = 20;

    private final static int threadTotal = 3;


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        //一次同时执行threadTotal个
        final Semaphore semaphore = new Semaphore(threadTotal);

        for (int i = 0; i < requestCount; i++) {
            final int threadNum = i;
            executorService.execute(() -> {
                try {
                    if(semaphore.tryAcquire(2,5000, TimeUnit.MILLISECONDS)){//总共5秒内拿到2个许可就做，拿不到丢弃
                        simuService(threadNum);
                        semaphore.release();//释放N个许可，这些许可都做完再做其他的
                    };
                } catch (Exception e) {
                    log.error("【Exception】:{}", e);
                }
            });
        }
        executorService.shutdown();
        log.info("Finished!");
    }




    private static void simuService(int threadNum) throws Exception {
        log.info("【ThreadNum】：{}", threadNum);
        Thread.sleep(10);
    }
}
