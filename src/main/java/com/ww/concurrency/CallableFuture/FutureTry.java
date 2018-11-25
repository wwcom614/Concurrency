package com.ww.concurrency.CallableFuture;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FutureTry {
    //相比Runnable接口，Callable接口可以获取线程返回值，功能更强大
    //注意Callable<>的类型要与call()的返回值类型一致，例如都是String
    static class MyCallable implements Callable<String>{
        @Override
        public String call() throws Exception {
            log.info("【Callable】:do something ...");
            Thread.sleep(5000);
            return "Done";
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
       //main方法主线程启动MyCallable线程，并设置Future接口用于届时获得MyCallable线程的返回值。
        Future<String> future = executorService.submit(new MyCallable());
        //main方法同时向下执行
        log.info("【Main】：do something ...");
        Thread.sleep(1000);
        //尝试查看并阻塞等待myCallabe执行结果
        String result = future.get();
        log.info("【Callable->Future】:{}",result);
        executorService.shutdown();
    }
}
