package com.kollu.springbootfilebulk.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 1. Allow your frontend origin
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        
        // 2. Allow common methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. Allow all headers (important for Auth tokens like 'Authorization')
        config.setAllowedHeaders(Arrays.asList("*"));
        
        //config.setExposedHeaders(Arrays.asList("Authorization"));
        // 4. Allow credentials if you use Cookies/Sessions
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
}