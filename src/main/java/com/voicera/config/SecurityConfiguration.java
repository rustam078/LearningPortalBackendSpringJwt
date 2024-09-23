package com.voicera.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.voicera.service.CustomOAuth2SuccessHandler;
import com.voicera.service.CustomOAuth2UserService;
//import com.voicera.service.OAuth2LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final InvalidUserAuthEntryPoint invalidUserAuthEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

  
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
        		.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", 
                                 "/forgetPassword/**", "/updatePassword","/userdtls/**" ,
                                 "/oauthSignin", "/user-info", 
                                 "/oauth2/**").permitAll()
//                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .requestMatchers("/test", "/skill", "/user", "/add", "/view")
                    .hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2
                    .successHandler(customOAuth2SuccessHandler)
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
            .exceptionHandling(config -> config.authenticationEntryPoint(invalidUserAuthEntryPoint))
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
            .build();
    }

    
}
