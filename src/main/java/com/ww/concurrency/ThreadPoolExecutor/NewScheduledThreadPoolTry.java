package com.ww.concurrency.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class NewScheduledThreadPoolTry {

    public static void main(String[] args) {

        log.info("Main Start");

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        //延迟3秒执行
/*        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("after 3 seconds execute");
            }
        },3, TimeUnit.SECONDS);
        executorService.shutdown();*/

        //按指定频率执行任务。例如延迟1秒执行，3秒执行一次
/*        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("延迟1秒执行，3秒执行一次");
            }
        },1,3,TimeUnit.SECONDS);*/

        //scheduleAtFixedRate与Timer功能类似，Timer相当于线程池newScheduledThreadPool(1)
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("【Timer】: Run per 5S");
            }
        },new Date(),5*1000);
    }
}
