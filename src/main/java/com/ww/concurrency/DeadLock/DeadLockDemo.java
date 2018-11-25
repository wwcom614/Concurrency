package com.ww.concurrency.DeadLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadLockDemo implements Runnable{
 /*
写一个简单的死锁
通过设置flag，让thread0先锁定o1,sleep 1秒
通过设置flag，让thread1先锁定o2，sleep 1秒
thread0 sleep结束后，需要再锁定o2才能继续执行，但此时o2已经被thread1锁定，thread0获取不到，于是thread0等待
thread1 sleep结束后，需要再锁定o1才能继续执行，但此时o1已经被thread0锁定，thread1获取不到，于是thread1等待
于是thread0和thread1互相等待，都需要得到对方资源才能继续运行，从而死锁
解决死锁方案：
1.锁定资源按同一顺序，例如下面加锁的顺序统一为先o1再o2
2.设置加锁超时时间，超过时间释放锁;例如下面不使用synchronized，用ReentrantLock.tryLock(long timeout, TimeUnit unit)
3.编写死锁检测代码，存储所有线程加锁记录，然后分析；如果发现死锁，释放所有锁，设置随机线程优先级，再次重试
PS：并发的地方留高级日志，便于排查问题
*/
    public int flag = 1;
    private static Object o1 = new Object(), o2 = new Object();

    @Override
    public void run() {
        log.info("【flag】:{}",flag);
        if(flag == 1){
            synchronized (o1){
                try{
                    log.info("locked o1");
                    Thread.sleep(1000);
                    log.info("sleep 1S");
                }catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (o2){
                    log.info("Then locked o2");//因为死锁，永远不会执行
                }
            }
        }
        if(flag == 2){
            synchronized (o2){
                try{
                    log.info("locked o2");
                    Thread.sleep(1000);
                    log.info("sleep 1S");
                }catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (o1){
                    log.info("Then locked o1");//因为死锁，永远不会执行
                }
            }
        }
    }

    public static void main(String[] args) {
        DeadLockDemo thread0 = new DeadLockDemo();
        DeadLockDemo thread1 = new DeadLockDemo();
        thread0.flag = 1;
        thread1.flag = 2;

        new Thread(thread0).start();
        new Thread(thread1).start();
    }

}
