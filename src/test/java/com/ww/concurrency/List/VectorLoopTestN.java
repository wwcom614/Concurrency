package com.ww.concurrency.List;

import java.util.Iterator;
import java.util.Vector;

//线程不安全。说明：Vector是线程安全容器。Vector是在内部方法上加了synchronized关键字
//但Vector内部多个方法之间调用时，乱序性会导致线程不安全

public class VectorLoopTestN {

    //foreach循环遍历vector，循环遍历过程中remove元素，线程不安全。
    // 建议循环遍历先做标记，循环结束后统一做remove
    private static void vectorLoop1Test(Vector<Integer> vector){
        for(Integer i: vector){
            if(i.equals(3)){
                vector.remove(i);
            }
        }
    }

    //迭代器Iterator循环遍历vector，循环遍历过程中remove元素，线程不安全
    // 建议循环遍历先做标记，循环结束后统一做remove
    private static void vectorLoop2Test(Vector<Integer> vector){
        Iterator<Integer> iterator = vector.iterator();
        while (iterator.hasNext()){
            Integer i = iterator.next();
            if(i.equals(3)){
                vector.remove(i);
            }
        }
    }

    //for循环遍历vector，循环遍历过程中remove元素，线程安全
     private static void vectorLoop3Test(Vector<Integer> vector){
        for(int i = 0; i < vector.size(); i++){
            if(vector.get(i).equals(3)){
                vector.remove(i);
            }
        }
    }

    public static void main(String[] args) {
        Vector<Integer> vector = new Vector<>();
        vector.add(1);
        vector.add(2);
        vector.add(3);

        //java.util.ConcurrentModificationException
        //vectorLoop1Test(vector);
        //vectorLoop2Test(vector);

        vectorLoop3Test(vector);
    }
}
