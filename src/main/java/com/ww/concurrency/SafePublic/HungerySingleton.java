package com.ww.concurrency.SafePublic;

import com.ww.concurrency.annoations.ThreadSafe;


//饿汉模式，单例在类装载时就创建好了，是线程安全的
//使用要求：1.私有构造函数处理不多，否则类加载慢，性能有问题；2.肯定会被使用，防止资源浪费
@ThreadSafe
public class HungerySingleton {

    //私有的构造方法，防止被外部对象调用创建实例
    private HungerySingleton() {
        //构造方法
    }

    //安全发布对象方法之一：在静态初始化方法中初始化一个对象引用
    //初始化私有的静态对象--单例对象
    private static HungerySingleton instance = new HungerySingleton();

    //静态的工厂方法
    public static HungerySingleton getInstance() {
        return instance;
    }
}
