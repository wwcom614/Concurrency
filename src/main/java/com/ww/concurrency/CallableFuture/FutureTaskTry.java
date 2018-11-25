package com.ww.concurrency.CallableFuture;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
public class FutureTaskTry {

    public static void main(String[] args) throws Exception {
        //定义线程获取结果任务，比使用Future灵活的是此处不一定是new Callable，也可以new Runable,result
        FutureTask<String> futureTask = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        log.info("【Callable】:do something ...");
                        Thread.sleep(5000);
                        return "Done";
                    }
                });
        //线程启动
        new Thread(futureTask).start();
        log.info("【Main】：do something 1 ...");
        Thread.sleep(1000);
        log.info("【Main】：do something 2 ...");
        //需要的时候获取线程执行结果
        String result = futureTask.get();
        log.info("【Future】:{}", result);
    }
}
