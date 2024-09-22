package com.voicera.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voicera.repository.UserRepository;
import com.voicera.user.Role;
import com.voicera.user.User;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	 private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load the OAuth2 user using the DefaultOAuth2UserService
    	logger.info("userRequestttt: {}", userRequest);
    	logger.info("getAccessToken: {}", userRequest.getAccessToken());
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract the email and name from the OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Process user registration and token generation logic
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return updateExistingUser(userOptional.get(), oAuth2User);
        } else {
            return registerNewUser(email, name, oAuth2User);
        }
    }

    @Transactional
    private OAuth2User registerNewUser(String email, String name, OAuth2User oAuth2User) {
        String generatedPassword = passwordGeneratorService.generatePassword(12);
        logger.info("register new user: {}",email);
        // Encode the password and create a new UserEntity
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstname(name);
        newUser.setLastname(name);
        newUser.setPassword(passwordEncoder.encode(generatedPassword));
        newUser.setRole(Role.ADMIN);
           

        userRepository.save(newUser);

        // Return a new DefaultOAuth2User with authorities
        return new DefaultOAuth2User(
            Set.of(new SimpleGrantedAuthority(Role.ADMIN.toString())),
            oAuth2User.getAttributes(),
            "email" // The key for the principal's name (e.g., "email" or "sub" for Google)
        );
    }

    @Transactional
    private OAuth2User updateExistingUser(User existingUser, OAuth2User oAuth2User) {
        // Optional logic for updating user attributes
    	logger.info("register existing user: {}",existingUser);
        existingUser.setFirstname(oAuth2User.getAttribute("name"));
        userRepository.save(existingUser);

        // Return the updated OAuth2User with authorities
        return new DefaultOAuth2User(
            Set.of(new SimpleGrantedAuthority(Role.ADMIN.toString())),
            oAuth2User.getAttributes(),
            "email" // The key for the principal's name
        );
    }
}
