package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import io.lettuce.core.*;

import org.example.filter.RateLimiterFilter;
import org.example.health.RedisHealthCheck;
import org.example.redis.RedisManager;
import org.example.repository.RateLimiterRepository;
import org.example.resource.TestResource;
import org.example.service.RateLimiterService;

public class RateLimiterApplication extends Application<RateLimiterConfiguration> {

    public static void main(String[] args) throws Exception {
        new RateLimiterApplication().run(args);
    }

    @Override
    public void run(RateLimiterConfiguration configuration, Environment environment) {
        RedisURI redisURI = RedisURI.Builder.redis("localhost", 6379).build();
        RedisManager redisManager = new RedisManager(redisURI);
        RateLimiterRepository repository = new RateLimiterRepository(redisManager, 60);
        RateLimiterService service = new RateLimiterService(repository, environment.metrics(), configuration);
        RateLimiterFilter filter = new RateLimiterFilter(service);

        environment.lifecycle().manage(redisManager);
        environment.jersey().register(filter);
        environment.jersey().register(new TestResource());
        environment.healthChecks().register("redis", new RedisHealthCheck(redisManager));
    }
}
