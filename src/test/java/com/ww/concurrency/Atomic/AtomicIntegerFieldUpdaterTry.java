package com.ww.concurrency.Atomic;

import com.ww.concurrency.annoations.ThreadSafe;
import com.ww.concurrency.model.AtomicIntegerFieldUpdaterModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;


//线程安全
@ThreadSafe
@Slf4j
//基于反射原子更新某个类的某个字段的值，注意该类中对该字段定义必须是public volatile
public class AtomicIntegerFieldUpdaterTry {

    //原子性更新某个类的实例中的某个字段，封装一下，下面调用的时候使用updater会很方便
    public static AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdaterModel> updater(String name) {
        return AtomicIntegerFieldUpdater.newUpdater(AtomicIntegerFieldUpdaterModel.class, name);
    }


    @Test
    public void AtomicIntegerFieldUpdaterTest(){

        AtomicIntegerFieldUpdaterModel atomicIntegerFieldUpdaterModel = new AtomicIntegerFieldUpdaterModel();
        log.info("publicVar = "+updater("publicVar").getAndAdd(atomicIntegerFieldUpdaterModel, 2));

       if(updater("publicVar").compareAndSet(atomicIntegerFieldUpdaterModel,5,100)){
            log.info("【1 Update Success】 publicVar={}",atomicIntegerFieldUpdaterModel.getPublicVar());
        };

        if(updater("publicVar").compareAndSet(atomicIntegerFieldUpdaterModel,5,200)){
            log.info("【2 Update Success】 publicVar={}",atomicIntegerFieldUpdaterModel.getPublicVar());
        }else {
            log.info("【2 Update Failed】 publicVar={}",atomicIntegerFieldUpdaterModel.getPublicVar());
        }




    }


}
