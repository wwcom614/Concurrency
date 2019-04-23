package com.ww.concurrency.Atomic;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

//线程安全
@ThreadSafe
@Slf4j
//对变量的原子更新
public class AtomicIntegerTestY {

    //请求总数
    public int REQ_TOTAL = 5000;
    //允许同时并发执行的线程数
    public int threadNum = 200;
    //使用原子计数
    public static AtomicInteger count = new AtomicInteger(0);

    @Test
    public void atomicIntegerTest(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        //使用Semaphore模拟允许同时并发执行的线程数
        final Semaphore semaphore = new Semaphore(threadNum);
        //使用CountDownLatch模拟请求总数
        final CountDownLatch countDownLatch = new CountDownLatch(REQ_TOTAL);

        for (int i = 0; i < REQ_TOTAL; i++ ){
            executorService.execute(() -> {
                try {
                    //控制模拟并发量：判断当前进程是否允许被执行，如果并发量超过预设值，进行阻塞等待
                    semaphore.acquire();
                    //如果acquireOK，才进行add
                    add();
                    //执行完毕add，释放
                    semaphore.release();
                }catch (Exception e){
                    log.error("【Exception】:{}",e);
                }
                //每执行完一次，减一个，控制模拟请求总数
                countDownLatch.countDown();
            });
        }

        try {
            //表示最终执行完
            countDownLatch.await();
            log.info("【count】：{}", count.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }


    }
    private void add() {
        //先获取当前值，再自增。本测试用哪个都行，有业务逻辑时要注意用哪个
        // count.getAndIncrement();
        //期望值是1，如果主内存是1，update为2019
        //count.compareAndSet(1,2019);
        //看看当前值到底是多少
        //count.get();
        //先自增，再获取当前值
        count.incrementAndGet();
    }

}
