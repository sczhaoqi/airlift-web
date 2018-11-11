package com.sc.zhaoqi.web.modules.discovery;

import com.google.inject.Inject;
import io.airlift.concurrent.Threads;
import io.airlift.discovery.client.ServiceInventory;
import io.airlift.discovery.client.ServiceInventoryConfig;
import io.airlift.http.server.HttpServerInfo;
import io.airlift.log.Logger;
import io.airlift.node.NodeInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DiscoveryUpdator
{
    private static final Logger log = Logger.get(DiscoveryUpdator.class);

    private ServiceInventory inventory;
    private HttpServerInfo info;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(Threads.daemonThreadsNamed("service-inventory-%s"));
    private ScheduledFuture<?> scheduledFuture;

    @Inject
    public DiscoveryUpdator(ServiceInventory inventory, HttpServerInfo info)
    {
        this.inventory = inventory;
        this.info = info;
    }

    private void updateServiceInventory()
            throws NoSuchFieldException, IllegalAccessException, URISyntaxException
    {
        Field inventoryField = inventory.getClass().getDeclaredField("serviceInventoryUri");
        inventoryField.setAccessible(true);
        inventoryField.set(inventory, info.getHttpUri());
        inventory.updateServiceInventory();
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
                log.error("failed update service Inventory"+ e);
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
