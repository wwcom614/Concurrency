package com.ww.concurrency.ThreadLocal;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//该filter要注册到启动类中生效，见启动类ConcurrencyApplication
@Slf4j
public class HttpFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //先将ServletRequest强制转换成HttpServletRequest，然后使用
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request.getSession().getAttribute("user");

        String currentReqPath = request.getServletPath();
        long currentThreadId = Thread.currentThread().getId();
        log.info("【HttpFilter-doFilter】: 当前线程thread id = {} ，当前请求路径{}",currentThreadId, currentReqPath);
        //将当前线程ID存储到RequestHolder中
        RequestHolder.add(Thread.currentThread().getId());
        //不拦截，让请求继续处理
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
