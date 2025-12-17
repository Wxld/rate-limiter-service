package org.example.repository;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.model.TokenBucketState;

public class RateLimiterRepository {

    private final RedisCommands<String, String> commands;
    private final int ttlSeconds;

    public RateLimiterRepository(StatefulRedisConnection<String, String> connection, int ttlSeconds) {
        this.commands = connection.sync();
        this.ttlSeconds = ttlSeconds;
    }

    public TokenBucketState getBucketState(String clientId) {
        String key = "rate_limit:" + clientId;
        String value = commands.get(key);

        if (value == null) {
            return null;
        }

        String[] parts = value.split(":");
        int tokens = Integer.parseInt(parts[0]);
        long timestamp = Long.parseLong(parts[1]);

        return new TokenBucketState(tokens, timestamp);
    }

    public void saveBucketState(String clientId, TokenBucketState state) {
        String key = "rate_limit:" + clientId;
        String value = state.getTokenCount() + ":" + state.getLastRefillTimestamp();

        commands.set(key, value);
        commands.expire(key, ttlSeconds);
    }
}