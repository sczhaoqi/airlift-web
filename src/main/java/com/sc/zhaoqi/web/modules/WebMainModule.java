package com.sc.zhaoqi.web.modules;

import com.google.inject.Binder;
import com.sc.zhaoqi.web.config.ServerConfig;
import io.airlift.configuration.AbstractConfigurationAwareModule;

import static io.airlift.configuration.ConfigBinder.configBinder;

public class WebMainModule
        extends AbstractConfigurationAwareModule
{
    @Override
    protected void setup(Binder binder)
    {
        configBinder(binder).bindConfig(ServerConfig.class);
    }
}
