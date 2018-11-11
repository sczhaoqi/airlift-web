package com.sc.zhaoqi.web.modules.discovery;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class DiscoveryUpdateModule
        implements Module
{

    @Override
    public void configure(Binder binder) {
        binder.bind(DiscoveryUpdator.class).in(Scopes.SINGLETON);
    }
}
