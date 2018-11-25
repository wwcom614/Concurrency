package com.ww.concurrency.model;

import lombok.Data;

@Data
public class AtomicIntegerFieldUpdaterModel {

    //只能用这种
    public volatile int publicVar=3;

    //AtomicIntegerFieldUpdaterTest中无法访问
    protected volatile int protectedVar=4;

    //AtomicIntegerFieldUpdaterTest中无法访问
    private volatile  int privateVar=5;

    //报java.lang.IllegalArgumentException
    public volatile static int staticVar = 10;

    //报异常：must be int
    public volatile Integer integerVar = 19;
    //报异常：must be int
    public volatile Long longVar = 18L;

}
