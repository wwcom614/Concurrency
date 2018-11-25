package com.ww.concurrency.SafePublic;

import com.ww.concurrency.annoations.Recommend;
import com.ww.concurrency.annoations.ThreadSafe;



//懒汉模式，单例在第一次使用时才创建
//为确保线程安全，
// 1.使用双重检测机制
// 2.使用volatile修饰instance，限制指令重排
@ThreadSafe
@Recommend
public class LazySingleton {

    //私有的构造方法，防止被外部对象调用创建实例。
    private LazySingleton() {
        //初始化处理，略
    }
    //安全发布对象方法之一：将对象的引用保存到volatile类型域或AtomicReference对象中
    //初始化私有的volatile(禁止指令重排)的静态对象--单例对象
    private volatile static LazySingleton instance = null;

/*    说明：双重同步锁还不能保证线程安全，
因为有JVM和CPU的指令重排存在，例如下述发布对象过程中，步骤2和3可以重排；
    1.memory = allocate() 分配对象内存空间
    2.初始化对象 instance()= null
    3.instance = memory 设置instance指向分配的内存空间
    所以需要在初始化私有的静态对象instance时，指定其为volatile，禁止JVM和CPU指令重排
    */

    //静态的工厂方法，性能考虑，不直接在该方法上synchronized，而是在最里面加锁，提升性能
    public static LazySingleton getInstance() {
        //为保证下方代码块线程安全，如果是在上面的静态方法上直接增加synchronized，可以保证线程安全，但性能不高，不推荐
        if (instance == null) {//双重检测机制：1.先判断是否被实例化
            //安全发布对象方法之一：在将对象的引用保存到一个由锁保护的域中
            synchronized (LazySingleton.class) {//2.未被实例化，加同步锁
                if (instance == null) {//3.加锁后，再判断一次是否在加锁期间被其他线程实例化
                    instance = new LazySingleton();//4.自己加锁后，未被其他线程实例化，可以放心实例化了--线程安全
                }
            }
        }
        return instance;
    }
}
