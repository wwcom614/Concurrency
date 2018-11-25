package com.ww.concurrency.SafePublic;

import com.ww.concurrency.annoations.Recommend;
import com.ww.concurrency.annoations.ThreadSafe;


//传统的两私有一公开（私有构造方法、私有静态实例(懒实例化/直接实例化)、公开的静态获取方法）
// 涉及线程安全问题（即使有多重检查锁也可以通过反射破坏单例），
// 目前最为安全的实现单例的方法是通过内部静态enum的方法来实现，因为JVM会保证enum不能被反射并且构造器方法只执行一次。
//推荐：枚举模式的单例是线程最安全的。实例调用时才初始化，又能绝对保证线程安全
@ThreadSafe
@Recommend
public class EnumSingleton {

    //私有的构造方法，防止被外部对象调用创建实例
    private EnumSingleton() {
        //初始化处理，略
    }

    private enum Singleton{
        INSTANCE;

        private EnumSingleton enumSingleton;

        //JVM会保证此方法绝对只调用一次
        Singleton(){
            enumSingleton = new EnumSingleton();
        }
        public EnumSingleton getInstance(){
            return enumSingleton;
        }
    }

    //静态的工厂方法
    public static EnumSingleton getInstance() {
        return Singleton.INSTANCE.getInstance();
    }
}
