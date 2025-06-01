package com.schulz.jserv.http;

public class JsrvUrl {

    public static interface JsrvUrlPart {
        
        String serialize();

        boolean match(String part);

    }

    public static class JsrvStaticUrlPart implements JsrvUrlPart {

        private final String part;

        public JsrvStaticUrlPart(String part) {
            this.part = part;
        }

        @Override
        public String serialize() {
            return part;
        }

        @Override
        public boolean match(String part) {
            return this.part.equals(part);
        }
    }

    public static class JsrvWildcardUrlPart implements JsrvUrlPart {

        public JsrvWildcardUrlPart() {}

        @Override
        public String serialize() {
            return "*";
        }

        @Override
        public boolean match(String part) {
            return true;
        }
    }

    private final JsrvUrlPart[] parts;

    private JsrvUrl(JsrvUrlPart[] parts) {
        this.parts = parts;
    }

    public static JsrvUrl parse(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        // Remove leading and trailing slashes
        url = url.trim();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String[] partStrings = url.split("/");
        JsrvUrlPart[] parts = new JsrvUrlPart[partStrings.length];
        for (int i = 0; i < partStrings.length; i++) {
            String part = partStrings[i];
            if ("*".equals(part)) {
                parts[i] = new JsrvWildcardUrlPart();
            } else {
                parts[i] = new JsrvStaticUrlPart(part);
            }
        }
        return new JsrvUrl(parts);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (JsrvUrlPart part : parts) {
            if (sb.length() > 0) {
                sb.append("/");
            }
            sb.append(part.serialize());
        }
        return sb.toString();
    }

    public boolean match(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Remove leading and trailing slashes
        url = url.trim();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String[] urlParts = url.split("/");

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].match(urlParts[i])) {
                return false;
            }
        }

        return true;
    }

    public String get(int index) {
        if (index < 0 || index >= parts.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return parts[index].serialize();
    }

}
