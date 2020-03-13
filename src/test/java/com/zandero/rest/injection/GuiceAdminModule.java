package com.zandero.rest.injection;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.zandero.rest.annotation.AuditLog;

public class GuiceAdminModule extends AbstractModule {

    @Override
    protected void configure() {
        LogInterceptor logInterceptor = new LogInterceptor();
        requestInjection(logInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(AuditLog.class), logInterceptor);
    }
}
