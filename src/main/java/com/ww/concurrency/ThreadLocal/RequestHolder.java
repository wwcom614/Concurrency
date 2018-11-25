package com.ww.concurrency.ThreadLocal;

//线程安全手段之一是线程封闭，例如threadlocal

//模拟互联网用户访问，服务器端为每个用户访问分配一个线程thread，
//用户请求最开始进入时(HttpFilter的doFilter)，获取并记录到该线程thread的ThreadLocal中
// 中间处理时随时可从ThreadLocal中取出使用
//最后(interceptor的afterCompletion)要记得释放
public class RequestHolder {

    private final static ThreadLocal<Long> requestHolder = new ThreadLocal<>();


    public static void add(Long id){
        requestHolder.set(id);
    }

    public static Long getId(){
        return requestHolder.get();
    }

    public static void remove(){
        requestHolder.remove();
    }
}
