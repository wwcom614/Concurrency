package com.ww.concurrency.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReentrantLockCondition {
    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        //从ReentrantLock实例中取出Condition
        Condition condition = reentrantLock.newCondition();

        new Thread(() -> {
            try {
                //线程1加入到AQS的等待队列中
                reentrantLock.lock();
                log.info("【thread1】: 1.wait for signal...");//1.等待信号
                //线程1从AQS等待队列中移除(释放锁)，紧接着线程1加入Condition的等待队列中
                condition.await();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            log.info("【thread1】: 4.get signal!");//4.get signal
            reentrantLock.unlock();
        }).start();

        new Thread(() -> {
            //线程1已经释放锁，线程2在AQS中被唤醒，尝试获取到了锁，线程2加入到AQS等待队列中
            reentrantLock.lock();
            log.info("【thread2】: 2.get lock!");//2.get lock
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            //线程2通知Condition等待队列中的所有线程；线程1被从Condition等待队列中移除，放入AQS等待队列，
            // 但此时线程1未被唤醒
            condition.signalAll();
            log.info("【thread2】: 3.send signal!");//3.send signal
            //线程2释放锁，线程1在AQS中被唤醒
            reentrantLock.unlock();
        }).start();

    }
}
