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
        // 1) Configure CORS (define CorsConfigurationSource bean if needed)
        http.cors(cors -> cors.configurationSource(request -> {
            var cfg = new CorsConfiguration();
            cfg.addAllowedOriginPattern("*");
            cfg.addAllowedMethod("*");
            cfg.addAllowedHeader("*");
            cfg.setAllowCredentials(true);
            return cfg;
        }));

        // 2) Disable CSRF protection
        http.csrf(csrf -> csrf.disable());

        // 3) Do not create sessions (JWT-based stateless authentication)
        http.sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 4) Configure endpoint access rules
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                // Only COMPANY_WORKER can register a company
                .requestMatchers(HttpMethod.POST, "/api/companies").hasRole("COMPANY_WORKER")
                // Only STORE_OWNER can register a store
                .requestMatchers(HttpMethod.POST, "/api/stores").hasRole("STORE_OWNER")
                // Any other request requires authentication
                .anyRequest().authenticated()
        );

        // 5) Add JWT filter before UsernamePasswordAuthenticationFilter
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
