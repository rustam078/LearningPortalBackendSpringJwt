package com.voicera.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
	private final InvalidUserAuthEntryPoint invalidUserAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer ::disable)
            .authorizeHttpRequests(authorize -> authorize
            		.requestMatchers("/api/v1/auth/**" ,"/v2/api-docs","/v2/api-docs/**", "/v3/api-docs", "/v3/api-docs/**","/v3/api-docs.yaml",  "/swagger-resources",
            			    "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**","/swagger-ui.html","/swagger-ui/index.html",
            			    "/webjars/**", "/swagger-ui.html","/api-docs/**","/forgetPassword/**","/updatePassword").permitAll()
                .requestMatchers("/test", "/skill", "/user", "/add", "/view").hasAnyAuthority("ADMIN", "USER") // Require ADMIN or USER role for these endpoints
                .anyRequest().authenticated() // All other requests require authentication
            )
         // Exception details
		     .exceptionHandling(config->config.authenticationEntryPoint(invalidUserAuthEntryPoint))
		     .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// register filter for 2nd request onwards

            .authenticationProvider(authenticationProvider) // Set authentication provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before UsernamePasswordAuthenticationFilter
			.logout(logout->logout.logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler) // Add logout handler
                .logoutSuccessHandler((request, response, authentication) ->
                    SecurityContextHolder.clearContext() // Clear security context on successful logout
                ))
            .build();
    }
}
