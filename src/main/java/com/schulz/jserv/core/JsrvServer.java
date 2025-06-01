package com.schulz.jserv.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;
import com.schulz.jserv.core.filter.JsrvFilter;
import com.schulz.jserv.core.filter.impl.JsrvCorsFilter;
import com.schulz.jserv.core.filter.impl.JsrvLoggingFilter;
import com.schulz.jserv.http.JsrvHttpMethod;
import com.schulz.jserv.security.cors.JsrvCorsConfig;

import io.undertow.Undertow;

public class JsrvServer {

    private static final Logger logger = LoggerFactory.getLogger(JsrvServer.class);

    private List<JsrvStartupTask> startupTasks;
    private Map<String, JsrvDispatcher> dispatchers;
    private List<JsrvFilter> filters;

    private JsrvServerConfig serverConfig;

    private Undertow server;

    private JsrvCorsConfig corsConfig;

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
        this.addFilter(new JsrvCorsFilter(this));

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
        readServerConfiguration();
        readCorsConfiguration();
    }

    private void readServerConfiguration() {

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

    private void readCorsConfiguration() {

        if (ApplicationConfig.getValue(JsrvCorsConfig.ENABLED_KEY) != Boolean.TRUE) {
            logger.info("CORS configuration is disabled, skipping CORS setup");
            return;
        }

        logger.info("Reading cors configuration");
        this.corsConfig = new JsrvCorsConfig();

        String allowedOrigin = ApplicationConfig.getValue(JsrvCorsConfig.ORIGIN_KEY);
        if (allowedOrigin == null || allowedOrigin.isEmpty()) {
            logger.warn("CORS origin configuration is missing, defaulting to '*'");
            allowedOrigin = "*"; // Default value
        }
        this.corsConfig.setAllowedOrigin(allowedOrigin);

        List<String> allowedMethodNames = ApplicationConfig.getValue(JsrvCorsConfig.METHODS_KEY);
        List<JsrvHttpMethod> allowedMethods = new ArrayList<>();
        if (allowedMethodNames != null && !allowedMethodNames.isEmpty()) {
            for (String methodName : allowedMethodNames) {
                if (JsrvHttpMethod.isValid(methodName)) {
                    allowedMethods.add(JsrvHttpMethod.fromString(methodName));
                } else {
                    logger.warn("Invalid HTTP method in CORS configuration: {}", methodName);
                }
            }
        }
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            logger.warn("CORS methods configuration is missing, defaulting to GET, POST, PUT, DELETE, OPTIONS");
            allowedMethods = List.of(JsrvHttpMethod.GET, JsrvHttpMethod.POST, JsrvHttpMethod.PUT, JsrvHttpMethod.DELETE, JsrvHttpMethod.OPTIONS);
        }
        this.corsConfig.setAllowedMethods(allowedMethods);

        List<String> allowedHeaders = ApplicationConfig.getValue(JsrvCorsConfig.HEADERS_KEY);
        if (allowedHeaders == null || allowedHeaders.isEmpty()) {
            logger.warn("CORS headers configuration is missing, defaulting to Content-Type, Authorization");
            allowedHeaders = List.of("Content-Type", "Authorization");
        }

        this.corsConfig.setAllowedHeaders(allowedHeaders);

        logger.info("Cors configuration loaded: origin={}, methods={}, headers={}", corsConfig.getAllowedOrigin(), corsConfig.getAllowedMethods().orElse(null), corsConfig.getAllowedHeaders().orElse(null));



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
        logger.info("Added filter {} at the end of the filter chain", filter.getKey());
    }

    public void addFilterAfterKey(String key, JsrvFilter filter) {
        if (key == null || key.isEmpty()) {
            logger.warn("Key is null or empty, adding filter at the end");
            filters.add(filter);
            return;
        }

        int index = -1;
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).getKey().equals(key)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            logger.warn("Key not found, adding filter at the end");
            filters.add(filter);
        } else {
            filters.add(index + 1, filter);
            logger.info("Added filter {} after key: {}", filter.getKey(), key);
        }
    }

    public void addFilterBeforeKey(String key, JsrvFilter filter) {
        if (key == null || key.isEmpty()) {
            logger.warn("Key is null or empty, adding filter at the end");
            filters.add(filter);
            return;
        }

        int index = -1;
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).getKey().equals(key)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            logger.warn("Key not found, adding filter at the end");
            filters.add(filter);
        } else {
            filters.add(index + 1, filter);
            logger.info("Added filter {} before: {}", filter.getKey(), key);
        }

    }

    public JsrvCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

}
