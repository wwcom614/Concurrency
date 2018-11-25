package com.ww.concurrency.List;


import com.ww.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

//ArrayList是线程不安全的
@NotThreadSafe
@Slf4j
public class ArrayListTestN {

    //请求总数
    public int requestTotal = 5000;
    //允许同时并发执行的线程数
    public int threadNum = 200;
    //全局静态变量，多线程测试，结果是线程不安全
    public static List<Integer> arrayList = new ArrayList<>();

    @Test
    public void ArrayListTest(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        //使用Semaphore模拟允许同时并发执行的线程数
        final Semaphore semaphore = new Semaphore(threadNum);
        //使用CountDownLatch模拟请求总数
        final CountDownLatch countDownLatch = new CountDownLatch(requestTotal);

        for (int i = 0; i < requestTotal; i++ ){
            final int count = i;
            executorService.execute(() -> {
                try {
                    //控制模拟并发量：判断当前进程是否允许被执行，如果并发量超过预设值，进行阻塞等待
                    semaphore.acquire();
                    //如果acquireOK，才进行add
                    add(count);
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
            log.info("【arrayList Size】：{}", arrayList.size());
            if(arrayList.size() == requestTotal){
                log.info("arrayList is thread safe!");
            }else {
                log.info("arrayList is NOT thread safe!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }


    }
    private void add(int i) {
        arrayList.add(i);
    }
}
