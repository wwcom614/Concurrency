package com.ww.concurrency.List;

import com.ww.concurrency.annoations.NotThreadSafe;

import java.util.Vector;

//线程不安全。说明：Vector是线程安全容器。Vector是在内部方法上加了synchronized关键字
//但Vector内部多个方法之间调用时，乱序性会导致线程不安全
@NotThreadSafe
public class VectorTestN {

    private static Vector<Integer> vector = new Vector<>();

    public static void main(String[] args) {
        while (true){
            for (int i = 0; i < 10; i++){
                vector.add(i);
            }

            Thread thread1 = new Thread(){
                public void run(){
                    for(int i = 0; i < vector.size(); i++){
                        vector.remove(i);
                    }
                }
            };

            Thread thread2 = new Thread(){
                public void run(){
                    for(int i = 0; i < vector.size(); i++){
                        vector.get(i);
                    }
                }
            };

            thread1.start();
            thread2.start();
        }
    }
}
