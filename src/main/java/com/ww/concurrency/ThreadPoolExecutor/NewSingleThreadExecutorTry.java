package com.ww.concurrency.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NewSingleThreadExecutorTry {

    public static void main(String[] args) {
        //线程顺序执行
        ExecutorService executorService = Executors.newSingleThreadExecutor();
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
