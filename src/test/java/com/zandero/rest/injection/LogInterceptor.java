package com.zandero.rest.injection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInterceptor implements MethodInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        /*TimeInterval timer = DateUtil.timer();
        timer.start();


        long costTime = timer.intervalMs();
        logger.info("cost time: {}", costTime);*/
        Object result = invocation.proceed();
        return result;
    }
}
