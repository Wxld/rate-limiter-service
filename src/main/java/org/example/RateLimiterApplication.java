package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;

import org.example.filter.RateLimiterFilter;
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
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        RateLimiterRepository repository = new RateLimiterRepository(connection, 60);

        RateLimiterService service = new RateLimiterService(repository);

        RateLimiterFilter filter = new RateLimiterFilter(service);

        environment.jersey().register(filter);
        environment.jersey().register(new TestResource());
    }
}
