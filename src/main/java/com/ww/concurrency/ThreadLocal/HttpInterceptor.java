package com.ww.concurrency.ThreadLocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//该HttpInterceptor要注册到启动类中生效，见启动类ConcurrencyApplication
@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Override
    //preHandle是接口请求之前处理
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("【HttpInterceptor-preHandle】:当前线程thread id = {}", RequestHolder.getId());
        //如果return false，就拦截，后续流程无法处理
        return true;
    }

    @Override
    //postHandle是接口正常处理之后
    //afterCompletion是无论接口正常或异常处理之后
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        RequestHolder.remove();
        log.info("【HttpInterceptor-afterCompletion】");
    }
}
