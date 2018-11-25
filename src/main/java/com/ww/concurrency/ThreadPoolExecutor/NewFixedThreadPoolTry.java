package com.ww.concurrency.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NewFixedThreadPoolTry {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for(int i = 0; i < 10; i++){
            final int taskNum = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("【Task】:{}",taskNum);
                }
            });
        }
        executorService.shutdown();
    }
}
