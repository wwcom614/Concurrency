package com.ww.concurrency.Lock;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

//线程安全
@ThreadSafe
@Slf4j
//对变量的原子更新
public class ReentrantLockTestY {

    //请求总数
    public static int requestTotal = 5000;
    //允许同时并发执行的线程数
    public static int threadNum = 200;
    //计数
    public static int count = 0;

    //默认不传fair参数或false，使用的是不公平锁；传入true，使用公平锁。
    private final static Lock lock = new java.util.concurrent.locks.ReentrantLock(true);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //使用Semaphore模拟允许同时并发执行的线程数
        final Semaphore semaphore = new Semaphore(threadNum);
        //使用CountDownLatch模拟请求总数
        final CountDownLatch countDownLatch = new CountDownLatch(requestTotal);

        for (int i = 0; i < requestTotal; i++) {
            executorService.execute(() -> {
                try {
                    //控制模拟并发量：判断当前进程是否允许被执行，如果并发量超过预设值，进行阻塞等待
                    semaphore.acquire();
                    //如果acquireOK，才进行add
                    add();
                    //执行完毕add，释放
                    semaphore.release();
                } catch (Exception e) {
                    log.error("【Exception】:{}", e);
                }
                //每执行完一次，减一个，控制模拟请求总数
                countDownLatch.countDown();
            });
        }

        try {
            //表示最终执行完
            countDownLatch.await();
            log.info("【count】：{}", count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }


    public static void add() {
        //如果使用tryLock()或tryLock(long,TimeUnit)表示如果没锁定才加锁
        //直接加锁ReentrantLock;
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}


