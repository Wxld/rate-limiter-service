package org.example.service;

import org.example.model.TokenBucketState;
import org.example.repository.RateLimiterRepository;

public class RateLimiterService {

    private RateLimiterRepository repository;
    private final int MAX_TOKENS = 10;
    private final int REFILL_RATE = 10;
    private final int refillIntervalMillis = 60_000;

    public RateLimiterService(RateLimiterRepository repository) {
        this.repository = repository;
    }

    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        TokenBucketState tokenBucketState = repository.getBucketState(clientId);
        if(tokenBucketState == null) {
            tokenBucketState = new TokenBucketState(MAX_TOKENS - 1, currentTime);
            repository.saveBucketState(clientId, tokenBucketState);
            return true;
        }
        long timeElapsed = currentTime - tokenBucketState.getLastRefillTimestamp();
        long tokenToAdd = (timeElapsed * REFILL_RATE) / refillIntervalMillis;

        long updateTokens = Math.min(MAX_TOKENS, tokenToAdd + tokenBucketState.getTokenCount());
        if(updateTokens == 0)
            return false;
        tokenBucketState = new TokenBucketState((int) (updateTokens - 1), currentTime);
        repository.saveBucketState(clientId, tokenBucketState);
        return true;
    }
}
