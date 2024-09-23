package com.voicera.authController;


import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.config.JwtService;
import com.voicera.repository.UserRepository;
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
    @Lazy
    private PasswordEncoder encoder;


@GetMapping("/userdtls/{email}")
public ResponseEntity<?> userToken(@PathVariable String email) {
	System.out.println("SsoOauthController.userToken() "+email);
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
