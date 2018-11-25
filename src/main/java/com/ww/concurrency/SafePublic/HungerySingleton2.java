package com.ww.concurrency.SafePublic;

import com.ww.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;


//饿汉模式，单例在类装载时就创建好了，是线程安全的
//使用要求：1.私有构造函数处理不多；2.肯定会被使用，防止资源浪费
//第二种饿汉模式的单例实现方式，注意顺序不能反：
//1.静态域初始化对象 instance()= null；
//2.静态代码块实例化单例
@ThreadSafe
@Slf4j
public class HungerySingleton2 {

    //私有的构造方法，防止被外部对象调用创建实例
    private HungerySingleton2() {
        //初始化处理，略
    }

    //1.初始化对象 instance()= null；
    private static HungerySingleton2 instance = null;

    //2.静态代码块实例化单例
    static {
        instance = new HungerySingleton2();
    }

    //静态的工厂方法
    public static HungerySingleton2 getInstance() {
        return instance;
    }


    public static void main(String[] args) {
        log.info(String.valueOf(getInstance().hashCode()));
        log.info(String.valueOf(getInstance().hashCode()));
    }
}
