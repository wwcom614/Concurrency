package com.ww.concurrency.Atomic;

import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;


//线程安全
@ThreadSafe
@Slf4j
public class AtomicReference {

    private static java.util.concurrent.atomic.AtomicReference<Integer> count = new java.util.concurrent.atomic.AtomicReference<>(0);

    @Test
    public void atomicReferenceTest(){
        count.compareAndSet(0,2); //count=2
        count.compareAndSet(0,1); //不执行
        count.compareAndSet(1,3); //不执行
        count.compareAndSet(2,4); //count=4
        count.compareAndSet(3,5); //不执行
        log.info("【count】:{}", count.get()); //所以结果会是4
    }


}
