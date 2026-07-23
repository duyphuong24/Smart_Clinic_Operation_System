package com.smartclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/health", "/api/v1/billing/payos-webhook").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/staff/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/audit-logs/**", "/api/v1/reports/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/doctors/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "MANAGER")
                        .requestMatchers("/api/v1/doctors/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/specialties/**", "/api/v1/rooms/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "CASHIER", "MANAGER")
                        .requestMatchers("/api/v1/specialties/**", "/api/v1/rooms/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/doctor-availabilities/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "MANAGER")
                        .requestMatchers("/api/v1/doctor-availabilities/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "MANAGER")
                        .requestMatchers("/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "MANAGER", "PATIENT")
                        .requestMatchers("/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "PATIENT")
                        .requestMatchers("/api/v1/queue-items/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")
                        .requestMatchers("/api/v1/visits/**", "/api/v1/encounters/**", "/api/v1/encounter-services/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/services/**").hasAnyRole("ADMIN", "DOCTOR", "CASHIER", "MANAGER")
                        .requestMatchers("/api/v1/services/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/invoices/**", "/api/v1/payments/**").hasAnyRole("ADMIN", "CASHIER", "MANAGER", "PATIENT", "RECEPTIONIST")
                        .requestMatchers("/api/v1/invoices/**", "/api/v1/payments/**").hasAnyRole("ADMIN", "CASHIER", "RECEPTIONIST")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/manager/staff/**", "/admin/staff/**", "/admin/services/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/audit-logs/**", "/reports/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/schedule/board").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST", "DOCTOR")
                        .requestMatchers("/consultation/**", "/encounters/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")
                        .requestMatchers("/billing/**", "/invoices/**", "/payments/**").hasAnyRole("ADMIN", "CASHIER", "MANAGER")
                        .requestMatchers("/patients/**", "/appointments/**", "/queue/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")
                        .requestMatchers("/my-portal/**").hasRole("PATIENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process-login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}