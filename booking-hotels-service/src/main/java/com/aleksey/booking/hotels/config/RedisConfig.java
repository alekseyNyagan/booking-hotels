package com.aleksey.booking.hotels.config;

import com.aleksey.booking.hotels.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.lang.NonNull;

import java.util.Collections;

@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisConfig.RefreshTokenKeyspaceConfiguration.class,
enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RequiredArgsConstructor
public class RedisConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());

        return new JedisConnectionFactory(configuration);
    }

    public class RefreshTokenKeyspaceConfiguration extends KeyspaceConfiguration {

        private static final String REFRESH_TOKEN_KEYSPACE = "refresh_tokens";

        @Override
        protected @NonNull Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(RefreshToken.class, REFRESH_TOKEN_KEYSPACE);

            keyspaceSettings.setTimeToLive(jwtProperties.getRefreshTokenExpiration().getSeconds());

            return Collections.singleton(keyspaceSettings);
        }
    }
}