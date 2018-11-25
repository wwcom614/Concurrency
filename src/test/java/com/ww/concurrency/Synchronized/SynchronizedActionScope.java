package com.ww.concurrency.Synchronized;


import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//线程安全
@ThreadSafe
@Slf4j
public class SynchronizedActionScope {

    //1.Synchronized修饰代码块，只作用于调用它的当前对象。所以多线程同时各自安全执行该代码块，且互不影响。
    public void syncBlock(int j) {
        synchronized (this) {
            for (int i = 0; i < 10; i++) {
                log.info("【Synchronized Block Test】：{} - {}", j, i);
            }
        }
    }

    @Test
    public void testSyncBlock() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        SynchronizedActionScope synchronizedActionScope1 = new SynchronizedActionScope();
        SynchronizedActionScope synchronizedActionScope2 = new SynchronizedActionScope();
        executorService.execute(() -> {
            synchronizedActionScope1.syncBlock(1);
        });
        executorService.execute(() -> {
            synchronizedActionScope2.syncBlock(2);
        });
        log.info("Test Over");
    }

/* ********************************************************************************** */

    //2.Synchronized修饰方法，只作用于调用它的当前对象。所以多线程同时各自安全执行该方法，且互不影响。
    //父类方法的Synchronized是不继承给子类的，因为Synchronized不属于方法声明的一部分
    public synchronized void syncMethod(int j) {
        for (int i = 0; i < 10; i++) {
            log.info("【Synchronized Method Test】：{} - {}",j, i);
        }
    }

    @Test
    public void testSyncMethod() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        SynchronizedActionScope synchronizedActionScope1 = new SynchronizedActionScope();
        SynchronizedActionScope synchronizedActionScope2 = new SynchronizedActionScope();
        executorService.execute(() -> {
            synchronizedActionScope1.syncMethod(1);
        });
        executorService.execute(() -> {
            synchronizedActionScope2.syncMethod(2);
        });
        log.info("Test Over");
    }
/* ********************************************************************************** */

    //3.Synchronized修饰静态方法，作用于调用它的所有对象。所以多线程中的一个线程先安全执行完该方法，另外一个线程再安全执行该方法。
    public static void syncStaticMethod(int j) {
        for (int i = 0; i < 10; i++) {
            log.info("【Synchronized Static Method Test】：{} - {}", j,i);
        }
    }

    @Test
    public void testStaticMethod() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        SynchronizedActionScope synchronizedActionScope1 = new SynchronizedActionScope();
        SynchronizedActionScope synchronizedActionScope2 = new SynchronizedActionScope();
        executorService.execute(() -> {
            synchronizedActionScope1.syncStaticMethod(1);;
        });
        executorService.execute(() -> {
            synchronizedActionScope2.syncStaticMethod(2);
        });

        log.info("Test Over");
    }



/* ********************************************************************************** */

    //4.Synchronized修饰类，作用于调用它的所有对象。所以多线程中的一个线程先安全执行完该类实例化的对象，另外一个线程再安全执行该类实例化的对象。
    public synchronized void syncClass(int j) {
        synchronized (SynchronizedActionScope.class) {
            for (int i = 0; i < 10; i++) {
                log.info("【Synchronized Class Test】：{} - {}",j, i);
            }
        }
    }

    @Test
    public void testClass() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        SynchronizedActionScope synchronizedActionScope1 = new SynchronizedActionScope();
        SynchronizedActionScope synchronizedActionScope2 = new SynchronizedActionScope();
        executorService.execute(() -> {
            synchronizedActionScope1.syncClass(1);;
        });
        executorService.execute(() -> {
            synchronizedActionScope2.syncClass(2);
        });

        log.info("Test Over");
    }



}
