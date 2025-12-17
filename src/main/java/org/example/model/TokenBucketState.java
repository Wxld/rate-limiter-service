package org.example.model;

public class TokenBucketState {
    private int tokenCount;
    private long lastRefillTimestamp;

    public TokenBucketState(int tokenCount, long lastRefillTimestamp) {
        this.tokenCount = tokenCount;
        this.lastRefillTimestamp = lastRefillTimestamp;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public long getLastRefillTimestamp() {
        return lastRefillTimestamp;
    }
}
