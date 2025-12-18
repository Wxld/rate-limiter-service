package org.example.service;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.example.RateLimiterConfiguration;
import org.example.config.RateLimitConfiguration;
import org.example.config.RateLimitRule;
import org.example.model.RateLimitKey;
import org.example.model.TokenBucketState;
import org.example.repository.RateLimiterRepository;

public class RateLimiterService {

    private final RateLimitConfiguration configuration;
    private final RateLimiterRepository repository;
    private final Counter allowedCounter;
    private final Counter rejectedCounter;
    private final Counter errorCounter;

    public RateLimiterService(RateLimiterRepository repository, MetricRegistry metricRegistry, RateLimiterConfiguration configuration) {
        this.configuration = configuration.rateLimitConfiguration;
        this.repository = repository;
        this.allowedCounter = metricRegistry.counter("rate_limiter.allowed");
        this.rejectedCounter = metricRegistry.counter("rate_limiter.rejected");
        this.errorCounter = metricRegistry.counter("rate_limiter.errors");
    }

    public boolean allowRequest(RateLimitKey key) {
        try {
            RateLimitRule rateLimitRule = configuration.perPath.getOrDefault(key.getPath(), configuration.defaultRule);
            String redisKey = key.toRedisKey();
            long currentTime = System.currentTimeMillis();
            TokenBucketState tokenBucketState = repository.getBucketState(redisKey);
            if (tokenBucketState == null) {
                tokenBucketState = new TokenBucketState(rateLimitRule.maxTokens - 1, currentTime);
                repository.saveBucketState(redisKey, tokenBucketState);
                allowedCounter.inc();
                return true;
            }
            long timeElapsed = currentTime - tokenBucketState.getLastRefillTimestamp();
            long tokenToAdd = (timeElapsed * rateLimitRule.refillRate) / configuration.refillIntervalMillis;
            long updateTokens = Math.min(rateLimitRule.maxTokens, tokenToAdd + tokenBucketState.getTokenCount());
            if (updateTokens == 0) {
                rejectedCounter.inc();
                return false;
            }
            tokenBucketState = new TokenBucketState((int) (updateTokens - 1), currentTime);
            repository.saveBucketState(redisKey, tokenBucketState);
            allowedCounter.inc();
            return true;
        } catch (Exception e) {
            errorCounter.inc();
            throw(e);
        }
    }
}
