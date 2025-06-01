package com.schulz.jserv.security.cors;

import java.util.List;

public abstract class JsrvAuthHandle {

    /**
     * Returns the user ID associated with the authentication handle.
     * 
     * @return the user ID as a Long
    */
    public abstract Long getUserId();

    /**
     * Returns a list of roles associated with the authentication handle.
     * 
     * @return a list of role names as Strings
     */
    public abstract List<String> getRoles();

    public boolean isAuthenticated() {
        return getUserId() != null;
    }

    public static class JsrvNotAuthenticatedHandle extends JsrvAuthHandle {

        private static final JsrvNotAuthenticatedHandle INSTANCE = new JsrvNotAuthenticatedHandle();

        private JsrvNotAuthenticatedHandle() {
            // Private constructor to prevent instantiation
        }

        public static JsrvNotAuthenticatedHandle getInstance() {
            return INSTANCE;
        }

        @Override
        public Long getUserId() {
            return null;
        }

        @Override
        public List<String> getRoles() {
            return List.of(); // No roles for unauthenticated handle
        }
    
    }

}
