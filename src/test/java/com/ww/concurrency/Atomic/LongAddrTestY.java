package com.ww.concurrency.Atomic;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.LongAdder;

//线程安全
@ThreadSafe
@Slf4j
public class LongAddrTestY {

    //请求总数
    public int requestTotal = 5000;
    //允许同时并发执行的线程数
    public int threadNum = 200;
    /*
    AtomicLong是在一个死循环内不断CAS判断，并发高时失败率高
    Long和Double JVM允许将64位拆成2个32位读写操作
    LongAddr将热点数据分离，例如分离成一个数组(多个单元的cell)，线程访问时Hash映射到某一个数组(每个cell独自维护自身值)计数，最终将拆分数组求和累加起来看是否一致
    优点是：通过将热点分离,将单点计算压力分散到各个节点上，提升了并行度和性能。
    缺点是统计数据有可能有误差
    所以，优先使用LongAddr
    在线程竞争低的情况下，或者需要准确数值例如序列号生成，使用AtomicLong
    */
    public static  LongAdder count = new LongAdder();

    @Test
    public void LongAddrTest(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        //使用Semaphore模拟允许同时并发执行的线程数
        final Semaphore semaphore = new Semaphore(threadNum);
        //使用CountDownLatch模拟请求总数
        final CountDownLatch countDownLatch = new CountDownLatch(requestTotal);

        for (int i = 0; i < requestTotal; i++ ){
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
            log.info("【count】：{}", count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }


    }
    private void add() {
        //先获取当前值，再自增。本测试用哪个都行，有业务逻辑时要注意用哪个
        // count.getAndIncrement()

        //先自增，再获取当前值
        count.increment();
    }

}
