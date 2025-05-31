package com.schulz.jserv.core;

public interface JsrvStartupTask {
    
    /**
     * Returns the name of the startup task.
     * 
     * @return the name of the startup task
     */
    String getName();

    /**
     * This method is called during the startup of the JServ server.
     * Implementations should perform any necessary initialization tasks.
     *
     * @param server the JsrvServer instance that is being initialized
     * 
     * @return true if the initialization was successful, false otherwise
     * 
     * @throws Exception if an error occurs during initialization
     */
    boolean run(JsrvServer server) throws Exception;
    
    /**
     * This method is called after all startup tasks have been completed.
     * 
     * @param server the JsrvServer instance that has been started
     * 
     * @return true if the post-initialization was successful, false otherwise
     * 
     * @throws Exception if an error occurs during post-initialization
     */
    default boolean postInit(JsrvServer server) throws Exception { return true; }

} 
