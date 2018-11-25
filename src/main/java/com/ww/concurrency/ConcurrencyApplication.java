package com.ww.concurrency;

import com.ww.concurrency.ThreadLocal.HttpFilter;
import com.ww.concurrency.ThreadLocal.HttpInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Configuration
public class ConcurrencyApplication implements WebMvcConfigurer {

    public static void main(String[] args) {

        SpringApplication.run(ConcurrencyApplication.class, args);
    }

    //过滤器Filter:依赖于servlet容器。在实现上基于函数回调，可以对几乎所有请求进行过滤，
    // 但是缺点是一个过滤器实例只能在容器初始化时调用一次。
    // 使用过滤器的目的是用来做一些过滤操作，获取我们想要获取的数据，
    // 比如：在过滤器中修改字符编码；在过滤器中修改HttpServletRequest的一些参数，包括：过滤低俗文字、危险字符等
    @Bean
    public FilterRegistrationBean httpFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        //设置自定义拦截器
        filterRegistrationBean.setFilter(new HttpFilter());
        //设置想拦截的url
        filterRegistrationBean.addUrlPatterns("/threadLocal/*");
        return filterRegistrationBean;
    }


    //拦截器interceptor:依赖于web框架，在SpringMVC中就是依赖于SpringMVC框架。
    // 在实现上基于Java的反射机制，属于面向切面编程（AOP）的一种运用。
    // 由于拦截器是基于web框架的调用，因此可以使用Spring的依赖注入（DI）进行一些业务操作，
    // 同时一个拦截器实例在一个controller生命周期之内可以多次调用。
    // 但是缺点是只能对controller请求进行拦截，对其他的一些比如直接访问静态资源的请求则没办法进行拦截处理
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpInterceptor()).addPathPatterns("/**");
    }



}
