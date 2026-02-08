package com.grape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/* 비밀 번호 인코더 */
@Configuration
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (필요 시 조정)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/user/**").permitAll();
                    auth.requestMatchers("/auth/**").permitAll();
                    auth.anyRequest().authenticated();
                });
        return http.build();
    }
}



