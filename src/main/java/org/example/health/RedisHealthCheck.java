package org.example.health;

import com.codahale.metrics.health.HealthCheck;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.redis.RedisManager;

public class RedisHealthCheck extends HealthCheck {
    private final RedisManager redisManager;

    public RedisHealthCheck(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    protected Result check() {
        try {
            String response = commands().ping();
            if("PONG".equalsIgnoreCase(response)) {
                return Result.healthy("Redis is healthy");
            }
            return Result.unhealthy("Unexpected Redis response: " + response);
        } catch (Exception e) {
            return Result.unhealthy("Redis unreachable: " + e.getMessage());
        }
    }

    private RedisCommands<String, String> commands() {
        return redisManager.getCommands();
    }
}
