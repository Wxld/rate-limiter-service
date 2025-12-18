package org.example.model;

public class RateLimitKey {

    private final String clientId;
    private final String method;
    private final String path;

    public RateLimitKey(String clientId, String method, String path) {
        this.clientId = clientId;
        this.method = method;
        this.path = path;
    }

    public String toRedisKey() {
        return String.format("rate_limiter:%s:%s:%s",
                clientId,
                method,
                path);
    }

    public String getPath() {
        return path;
    }
}
