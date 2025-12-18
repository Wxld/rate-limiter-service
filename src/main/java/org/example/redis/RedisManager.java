package org.example.redis;

import io.dropwizard.lifecycle.Managed;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;

public class RedisManager implements Managed {

    private final RedisURI redisURI;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    public RedisManager(RedisURI redisURI) {
        this.redisURI = redisURI;
    }

    @Override
    public void start() {
        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .build())
                .build();
        redisClient = RedisClient.create(redisURI);
        redisClient.setOptions(clientOptions);
        connection = redisClient.connect();
        connection.setTimeout(Duration.ofSeconds(2));
    }

    @Override
    public void stop() {
        if(redisClient != null)
            redisClient.shutdown();
        if(connection != null)
            connection.close();
    }

    public RedisCommands<String, String> getCommands() {
        if(connection == null) {
            throw new IllegalStateException("Redis not initialized yet");
        }
        return connection.sync();
    }
}
