package com.ww.concurrency.Map;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.*;

//ConcurrentSkipListMap是线程安全的。
//相对ConcurrentHashMap性能略低。但
//1.key有序；
//2.性能与并发无关，所以可以承载更高的并发

@ThreadSafe
@Slf4j
public class ConcurrentSkipListMapTestY {

    //请求总数
    public int requestTotal = 5000;
    //允许同时并发执行的线程数
    public int threadNum = 200;
    //全局静态变量，多线程测试，结果是线程安全
    public static Map<Integer,String> map = new ConcurrentSkipListMap<>();

    @Test
    public void ConcurrentSkipListMapTest(){
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
            log.info("【Map Size】：{}", map.size());
            if(map.size() == requestTotal){
                log.info("ConcurrentSkipListMap is thread safe!");
            }else {
                log.info("ConcurrentSkipListMap is NOT thread safe!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }


    }
    private void add(int i) {
        map.put(i,"a");
    }
}
