package com.kollu.springbootfilebulk.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheWiper implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        try {
            redisTemplate.execute((RedisConnection connection) -> {
                connection.serverCommands().flushDb();
                return null;
            });
            System.out.println("✅ Redis Cache cleared successfully using non-deprecated commands.");
        } catch (Exception e) {
            System.err.println("❌ Could not clear Redis on startup: " + e.getMessage());
        }
    }
}