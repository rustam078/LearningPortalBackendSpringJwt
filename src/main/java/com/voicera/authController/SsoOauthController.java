package com.voicera.authController;


import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.voicera.config.JwtService;
import com.voicera.repository.UserRepository;
import com.voicera.service.PasswordGeneratorService;
import com.voicera.token.Token;
import com.voicera.token.TokenRepository;
import com.voicera.token.TokenType;
import com.voicera.user.Role;
import com.voicera.user.User;

@RestController
public class SsoOauthController {
    private static final Logger logger = LoggerFactory.getLogger(SsoOauthController.class);

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
	private  TokenRepository tokenRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private PasswordEncoder encoder;


    @Autowired
    private PasswordGeneratorService passwordGeneratorService;


@GetMapping(value = "/oauthSignin", produces = "application/json")
public ResponseEntity<?> authenticateUser(Principal principal) {
    logger.info("Received request to authenticate user.");
    logger.info("Principal: {}", principal);
    // OAuth2 logic here
    if (principal instanceof OAuth2AuthenticationToken) {
        logger.info("Principal is of type OAuth2AuthenticationToken.");
        OAuth2User oauthUser = ((OAuth2AuthenticationToken) principal).getPrincipal();
        String email = oauthUser.getAttribute("email");
        logger.info("Extracted email from OAuth2 user: {}", email);
        String generatedPassword = passwordGeneratorService.generatePassword(12);
        logger.info("Generated password for user: {}", generatedPassword);
        saveOAuthUser(principal, generatedPassword);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, generatedPassword));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("User authenticated successfully.");

        var user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User not found for email: {}", email);
            return new UsernameNotFoundException("User not found");
        });
        
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        
        AuthenticationResponse autresp = new AuthenticationResponse();	
        BeanUtils.copyProperties(user, autresp);
        autresp.setToken(jwtToken);
        
        Set<Role> rolesSet = new HashSet<>();
        rolesSet.add(user.getRole());
        autresp.setRoles(rolesSet);

        logger.info("Returning authentication response for user: {}", email);
        return new ResponseEntity<>(autresp, HttpStatus.OK);
    }
    
    logger.warn("Invalid authentication attempt.");
    return ResponseEntity.badRequest().body("Invalid authentication.");
}
    

    @GetMapping("/user-info")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @Transactional
    public String saveOAuthUser(Principal principal, String generatedPassword) {
        String encodedPassword = encoder.encode(generatedPassword);
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oauthUser = ((OAuth2AuthenticationToken) principal).getPrincipal();
            String email = oauthUser.getAttribute("email");
            String username = email;

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setPassword(encodedPassword);
                userRepository.save(existingUser);
                logger.info("User updated: {}", existingUser.getEmail());
            } else {
                User newUser = new User();
                newUser.setEmail(username);
                newUser.setFirstname(username);
                newUser.setLastname(username);
                newUser.setPassword(encodedPassword);
                newUser.setRole(Role.ADMIN);
                userRepository.save(newUser);
                logger.info("User saved: {}", newUser.getEmail());
            }
            return "User saved/updated successfully";
        }
        return "Invalid principal";
    }
    

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}
}
