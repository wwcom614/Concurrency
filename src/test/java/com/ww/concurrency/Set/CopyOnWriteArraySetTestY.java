package com.ww.concurrency.Set;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.*;

/*CopyOnWriteArraySet是线程安全的。慎用
先开辟一块空间复制数据写，然后将原数据引用指向新的这块空间
读写分离，最终一致性。对应对应排重不排序的HashSet
缺点：
1.内存消耗大 ，如果数据量大，有可能GC
2.写入后读实时性略差
适合读多写少的场景
写需要加锁，读不需要加锁
ReentrantLock
*/
@ThreadSafe
@Slf4j
public class CopyOnWriteArraySetTestY {

    //请求总数
    public int requestTotal = 5000;
    //允许同时并发执行的线程数
    public int threadNum = 200;
    //全局静态变量，多线程测试，结果是线程安全
    public static Set<Integer> set = new CopyOnWriteArraySet<>();

    @Test
    public void CopyOnWriteArraySetTest(){
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
            log.info("【Set Size】：{}", set.size());
            if(set.size() == requestTotal){
                log.info("copyOnWriteSet is thread safe!");
            }else {
                log.info("copyOnWriteSet is NOT thread safe!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }


    }
    private void add(int i) {
        set.add(i);
    }
}
