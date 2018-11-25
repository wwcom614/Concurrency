package com.ww.concurrency.ForkJoin;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/*Fork/Join类似Map/Reduce
使用了双端窃取算法，先完成任务的线程1不闲着，窃取未完成任务的线程2的任务执行
为避免冲突，未完成任务的线程2从队列头部执行任务，完成任务来帮忙的线程1从队列尾部执行任务*/
@Slf4j
//Recursive递归，不断将大任务拆分成小任务
public class ForkJoinTaskTry extends RecursiveTask<Integer> {

    public static final int threshold = 2;
    private int start;
    private int end;

    public ForkJoinTaskTry(int start, int end){
        this.start = start;
        this.end = end;
    }

    protected Integer compute(){
        int sum = 0;

        //如果任务数 <= 阈值threshold，直接计算任务
        boolean canCompute = (end - start) <= threshold;
        if(canCompute){
            for(int i = start; i <= end; i++){
                sum += i;
            }
        }else{//如果任务数 > 阈值threshold，拆分成2个任务再计算
            int middle = (start + end)/2;
            ForkJoinTaskTry task1 = new ForkJoinTaskTry(start, middle);
            ForkJoinTaskTry task2 = new ForkJoinTaskTry(middle+1, end);

            //执行拆分后的任务
            task1.fork();
            task2.fork();

            //等待任务执行完成后记录结果
            int task1Result = task1.join();
            int task2Result = task2.join();

            //合并结果
            sum = task1Result + task2Result;
        }
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        //计算任务，从1加到100
        ForkJoinTaskTry task = new ForkJoinTaskTry(1,100);
        //执行任务
        Future<Integer> result = forkJoinPool.submit(task);

        try{
            log.info("【Result】:{}", result.get());
        }catch (Exception e){
            log.error("【Exception】",e);
        }
    }
}
