package org.example.config;

import java.util.Map;

public class RateLimitConfiguration {

    public int refillIntervalMillis;
    public RateLimitRule defaultRule;
    public Map<String, RateLimitRule> perPath;
}
