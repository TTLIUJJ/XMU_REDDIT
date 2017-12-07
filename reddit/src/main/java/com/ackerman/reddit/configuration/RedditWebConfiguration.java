package com.ackerman.reddit.configuration;

import com.ackerman.reddit.interceptor.LoginRequiredInterceptor;
import com.ackerman.reddit.interceptor.PassPortInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class RedditWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassPortInterceptor passPortInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passPortInterceptor);

        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/cherry")
                .addPathPatterns("/conversation/*");

        super.addInterceptors(registry);
    }
}
