package com.schulz.jserv.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;
import com.schulz.jserv.core.filter.JsrvFilter;
import com.schulz.jserv.core.filter.impl.JsrvLoggingFilter;

import io.undertow.Undertow;

public class JsrvServer {

    private static final Logger logger = LoggerFactory.getLogger(JsrvServer.class);

    private List<JsrvStartupTask> startupTasks;
    private Map<String, JsrvDispatcher> dispatchers;
    private List<JsrvFilter> filters;

    private JsrvServerConfig serverConfig;

    private Undertow server;

    public JsrvServer() {
        this.startupTasks = new ArrayList<>();
        this.dispatchers = new HashMap<>();
        this.filters = new ArrayList<>();
    }

    public void start() throws Exception {

        logger.info("Starting JServ server");
    
        // initialization
        init();

        this.server = Undertow.builder()
                .addHttpListener(serverConfig.getPort(), serverConfig.getHost())
                .setHandler(new JsrvHttpHandler(this))
                .setServerOption(io.undertow.UndertowOptions.ENABLE_HTTP2, true)
                .build();

        this.server.start();
        logger.info("JServ server started on {}:{}", serverConfig.getHost(), serverConfig.getPort());
    }

    private void init() throws Exception {

        
        logger.info("Initializing JServ server");

        this.addFilter(new JsrvLoggingFilter());
        
        // read configuration
        readConfiguration();
        
        performStartupTasks();
        
        performPostInitTasks();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.shutdown();
        }));
        logger.info("JServ server initialized successfully");
    }

    private void shutdown() {
        logger.info("Jserv server shutdown");
    }

    private void readConfiguration() {

        logger.info("Reading server configuration");

        String host = ApplicationConfig.getValue(JsrvServerConfig.HOST_KEY);
        Integer port = ApplicationConfig.getValue(JsrvServerConfig.PORT_KEY);
        
        if (host == null) {
            logger.error("Server host configuration is missing");
            throw new RuntimeException("Server host configuration is missing");
        }
        if (port == null) {
            logger.error("Server port configuration is missing");
            throw new RuntimeException("Server port configuration is missing");
        }
        this.serverConfig = new JsrvServerConfig(host, port);

        logger.info("Server configuration loaded: host={}, port={}", serverConfig.getHost(), serverConfig.getPort());

    }

    private long performStartupTasks() throws Exception {
        long totalTime = 0;

        for (JsrvStartupTask task : startupTasks) {
            long startTime = System.currentTimeMillis();
            logger.info("Executing startup task: {}", task.getName());
            boolean success = task.run(this);
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            totalTime += duration;

            if (!success) {
                logger.error("Startup task {} failed", task.getName());
                throw new Exception("Startup task failed: " + task.getName());
            } else {
                logger.info("Startup task {} completed in {} seconds", task.getName(), duration);
            }
        }

        return totalTime;        
    }

    private void performPostInitTasks() throws Exception {
        for (JsrvStartupTask task : startupTasks) {
            logger.info("Executing post-initialization task: {}", task.getName());
            boolean success = task.postInit(this);
            if (!success) {
                logger.error("Post-initialization task {} failed", task.getName());
                throw new Exception("Post-initialization task failed: " + task.getName());
            }
        }
    }

    public void addStartupTask(JsrvStartupTask task) {
        if (task != null) {
            startupTasks.add(task);
            logger.info("Added startup task: {}", task.getName());
        } else {
            logger.warn("Attempted to add a null startup task");
        }
    }

    public void addDispatcher(JsrvDispatcher dispatcher) {
        if (dispatcher == null) {
            logger.warn("Attempted to add a null dispatcher");
            return;
        }
        dispatchers.put(dispatcher.getBasePath(), dispatcher);
        logger.info("Added dispatcher: {}", dispatcher.getBasePath());
    }

    public JsrvDispatcher getDispatcher(String dispatcherName) {
        if (dispatcherName == null) {
            logger.warn("Dispatcher name is null or empty");
            return null;
        }
        JsrvDispatcher dispatcher = dispatchers.get(dispatcherName);
        
        return dispatcher;
    }

    public List<JsrvFilter> getFilters() {
        return filters;
    }

    public void addFilter(JsrvFilter filter) {
        this.filters.add(filter);
    }


}
