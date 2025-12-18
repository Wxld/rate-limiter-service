package org.example;

import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.config.RateLimitConfiguration;

public class RateLimiterConfiguration extends Configuration {

    @Valid
    @NotNull
    public RateLimitConfiguration rateLimitConfiguration;
}
