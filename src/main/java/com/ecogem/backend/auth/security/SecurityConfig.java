package com.ecogem.backend.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1) CORS 설정 (필요에 따라 CorsConfigurationSource 빈 정의)
        http.cors(cors -> {
            cors.configurationSource(request -> {
                var cfg = new CorsConfiguration();
                cfg.addAllowedOriginPattern("*");
                cfg.addAllowedMethod("*");
                cfg.addAllowedHeader("*");
                cfg.setAllowCredentials(true);
                return cfg;
            });
        });

        // 2) CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 3) 세션을 생성하지 않음 (JWT Stateless)
        http.sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 4) 엔드포인트 권한 설정
        http.authorizeHttpRequests(authz ->
                authz
                        .requestMatchers("/api/auth/**").permitAll()
                        // COMPANY_WORKER 만 회사 등록 가능
                        .requestMatchers(HttpMethod.POST, "/api/companies").hasRole("COMPANY_WORKER")
                        // STORE_OWNER 만 가게 등록 가능
                        .requestMatchers(HttpMethod.POST, "/api/stores").hasRole("STORE_OWNER")
                        // 나머지 API 들은 인증만 있으면 접근
                        .anyRequest().authenticated()
        );

        // 5) JWT 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
