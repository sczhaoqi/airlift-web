package com.sc.zhaoqi.web;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.sc.zhaoqi.web.modules.WebMainModule;
import com.sc.zhaoqi.web.modules.discovery.DiscoveryUpdateModule;
import io.airlift.bootstrap.Bootstrap;
import io.airlift.discovery.client.Announcer;
import io.airlift.discovery.client.DiscoveryModule;
import io.airlift.discovery.server.EmbeddedDiscoveryModule;
import io.airlift.event.client.HttpEventModule;
import io.airlift.event.client.JsonEventModule;
import io.airlift.http.server.HttpServerModule;
import io.airlift.jaxrs.JaxrsModule;
import io.airlift.jmx.JmxHttpModule;
import io.airlift.jmx.JmxModule;
import io.airlift.json.JsonModule;
import io.airlift.log.LogJmxModule;
import io.airlift.log.Logger;
import io.airlift.node.NodeModule;
import io.airlift.tracetoken.TraceTokenModule;
import org.weakref.jmx.guice.MBeanModule;

import static com.sc.zhaoqi.web.system.SystemRequirements.verifyJvmRequirements;
import static com.sc.zhaoqi.web.system.SystemRequirements.verifySystemTimeIsReasonable;

public class AirliftWebServer
        implements Runnable
{
    public static void main(String[] args)
    {
        new AirliftWebServer().run();
    }

    @Override
    public void run()
    {
        verifyJvmRequirements();
        verifySystemTimeIsReasonable();

        Logger log = Logger.get(AirliftWebServer.class);

        ImmutableList.Builder<Module> modules = ImmutableList.builder();

        modules.add(
                new NodeModule(),
                new DiscoveryModule(),
                new HttpServerModule(),
                new JsonModule(),
                new JaxrsModule(true),
                new MBeanModule(),
                new JmxModule(),
                new JmxHttpModule(),
                new LogJmxModule(),
                new TraceTokenModule(),
                new JsonEventModule(),
                new HttpEventModule(),
                // self module
                new WebMainModule(),
                new EmbeddedDiscoveryModule(),
                new DiscoveryUpdateModule());

        modules.addAll(getAdditionalModules());
        Bootstrap app = new Bootstrap(modules.build());
        loadProperties();
        try {
            Injector injector = app.strictConfig().initialize();
            injector.getInstance(Announcer.class).start();

            log.info("======== SERVER STARTED ========");
        }
        catch (Throwable e) {
            log.error(e);
            System.exit(1);
        }
    }

    private void loadProperties()
    {
        // we can using map or set -Dconfig=file-to-path
        // way 1.
        //app.setRequiredConfigurationProperties(properties);
        //way 2.
        System.setProperty("config",
                System.getProperty("config") == null ? this.getClass().getClassLoader().getResource("base.properties").getPath()
                        : System.getProperty("config"));
    }

    protected Iterable<? extends Module> getAdditionalModules()
    {
        return ImmutableList.of();
    }
}
