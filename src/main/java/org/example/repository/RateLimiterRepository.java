package org.example.repository;

import io.lettuce.core.api.sync.RedisCommands;
import org.example.model.TokenBucketState;
import org.example.redis.RedisManager;

public class RateLimiterRepository {

    private final RedisManager redisManager;
    private final int ttlSeconds;

    public RateLimiterRepository(RedisManager redisManager, int ttlSeconds) {
        this.redisManager = redisManager;
        this.ttlSeconds = ttlSeconds;
    }

    public TokenBucketState getBucketState(String key) {
        String value = commands().get(key);
        if (value == null) {
            return null;
        }
        String[] parts = value.split(":");
        int tokens = Integer.parseInt(parts[0]);
        long timestamp = Long.parseLong(parts[1]);

        return new TokenBucketState(tokens, timestamp);
    }

    public void saveBucketState(String key, TokenBucketState state) {
        String value = state.getTokenCount() + ":" + state.getLastRefillTimestamp();
        commands().set(key, value);
        commands().expire(key, ttlSeconds);
    }

    private RedisCommands<String, String> commands() {
        return redisManager.getCommands();
    }
}