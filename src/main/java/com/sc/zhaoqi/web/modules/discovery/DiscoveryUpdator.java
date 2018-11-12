package com.sc.zhaoqi.web.modules.discovery;

import com.google.inject.Inject;
import io.airlift.concurrent.Threads;
import io.airlift.discovery.client.DiscoveryClientConfig;
import io.airlift.http.server.HttpServerInfo;
import io.airlift.log.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DiscoveryUpdator
{
    private static final Logger log = Logger.get(DiscoveryUpdator.class);

    private final DiscoveryClientConfig config;
    private HttpServerInfo info;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(Threads.daemonThreadsNamed("service-inventory-%s"));
    private ScheduledFuture<?> scheduledFuture;

    @Inject
    public DiscoveryUpdator(DiscoveryClientConfig config, HttpServerInfo info)
    {
        this.info = info;
        this.config = config;
    }

    private void updateServiceInventory()
    {
        config.setDiscoveryServiceURI(info.getHttpUri());
    }

    @PostConstruct
    public synchronized void start()
    {

        this.scheduledFuture = this.executorService.scheduleAtFixedRate(() -> {
            try {
                log.info("1");
                updateServiceInventory();
            }
            catch (Exception e) {
                log.error("failed update service Inventory" + e);
            }
        }, 1000, 3000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public synchronized void stop()
    {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
            this.scheduledFuture = null;
        }
    }
}
