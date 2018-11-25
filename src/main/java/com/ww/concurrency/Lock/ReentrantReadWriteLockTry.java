package com.ww.concurrency.Lock;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTry {

    private final Map<String, Data> map = new TreeMap<>();

    //如果没有读和写lock才进行写操作。是一种悲观锁，如果是读多写少场景，写会很难抢到，写饥饿。使用场景不多
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();


    //读取一个数据
    public Data get(String key){
        //读取数据加读锁
        reentrantReadWriteLock.readLock().lock();
        try{
            return map.get(key);
        }finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    //读取所有数据
    public Set<String> getAllKeys(){
        //读取数据加读锁
        reentrantReadWriteLock.readLock().lock();
        try{
            return map.keySet();
        }finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    public Data put(String key, Data value){
        //写入数据加写锁
        reentrantReadWriteLock.writeLock().lock();
        try{
            return map.put(key,value);
        }finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    class Data {

    }
}
