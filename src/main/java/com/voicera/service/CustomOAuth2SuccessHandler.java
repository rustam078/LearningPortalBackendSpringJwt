package com.voicera.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);
    @Value("${ui.domain.path}")
    private String uiPath;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordGeneratorService passwordGeneratorService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        logger.info("OAuth2 authentication successful");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            logger.info("Authenticated user email: {}", email);
            String password = passwordGeneratorService.generatePassword(20);
            String encodeurl = passwordEncoder.encode(password);
            String redirectUrl = uiPath+"/oauth2/callback?clentId= "+encodeurl.concat(encodeurl)+" &code=" + email;
            response.sendRedirect(redirectUrl);
        }
    }
    
}
