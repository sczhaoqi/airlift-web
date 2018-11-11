package com.sc.zhaoqi.web.config;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

public class ServerConfig
{
    private String appName;
    private Boolean discovery;

    public String getAppName()
    {
        return appName;
    }

    @Config("app.name")
    @ConfigDescription("now app's name")
    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public Boolean getDiscovery()
    {
        return discovery;
    }

    @Config("discovery-server.enabled")
    @ConfigDescription("start as discovery")
    public void setDiscovery(Boolean discovery)
    {
        this.discovery = discovery;
    }
}
