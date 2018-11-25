package com.ww.concurrency.Lock;

import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {
    class Point{
        private double x,y;
        private final StampedLock sl = new StampedLock();

        //写操作
        void move(double deltaX, double deltaY){
            //lock时，会生成long类型的stamp
            long stamp = sl.writeLock();
            try{
                x += deltaX;
                y += deltaY;
            }finally {
                //解对应stamp的锁
                sl.unlockWrite(stamp);
            }
        }

        //读操作
        double distanceFromOrigin(){//只是读的方法
            long stamp = sl.tryOptimisticRead();//首先尝试获取读乐观锁
            double currentX = x, currentY = y;//将两个字段读入本地局部变量
            if(!sl.validate(stamp)){//检查发出读乐观锁后，与此同时是否有其他写锁发生
                stamp = sl.readLock();//如果没有，接下来获取一个读悲观锁
                try{
                    currentX = x;//将两个字段读入本地局部变量
                    currentY = y;//将两个字段读入本地局部变量
                }finally {
                    sl.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }

        //更新操作
        void moveIfAtOrigin(double newX,double newY){//更新操作
            //先拿读锁尝试，如果不行再用写锁锁
            // 可以替换为sl.tryOptimisticRead()
            long stamp = sl.readLock();
            try{
                while ( x == 0.0  && y == 0.0){//判断当前是否为圆中心点
                    long ws = sl.tryConvertToWriteLock(stamp);//将读锁转换为写锁
                    if(ws != 0L){//判断转换为写锁是否成功
                        stamp = ws; //如果转换为写锁成功，锁的stamp替换为写锁的stamp
                        x = newX;//从圆点移动到指定点
                        y = newY;//从圆点移动到指定点
                        break;
                    }else {//如果转换为写锁失败
                        sl.unlockRead(stamp);//显式释放读锁
                        stamp = sl.writeLock();//显式尝试加上更加严格的写锁
                    }
                }
            }finally {
                sl.unlock(stamp);//最终要释放所有的读锁和写锁
            }
        }
    }
}
