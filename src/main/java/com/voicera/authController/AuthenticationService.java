package com.voicera.authController;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.voicera.config.JwtService;
import com.voicera.repository.UserRepository;
import com.voicera.token.Token;
import com.voicera.token.TokenRepository;
import com.voicera.token.TokenType;
import com.voicera.user.Role;
import com.voicera.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole()).build();
		var savedUser = repository.save(user);
//    var jwtToken = jwtService.generateToken(user);
//    saveUserToken(savedUser, jwtToken);
//    return AuthenticationResponse.builder()
//        .token(jwtToken)
//        .build();
		return AuthenticationResponse.builder().token("sucessfull register...").build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		// if above line execute means user get authenticated
		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
	    
		AuthenticationResponse autresp = new AuthenticationResponse();	
		BeanUtils.copyProperties(user, autresp);
		autresp.setToken(jwtToken);
		Set<Role> rolesSet = new HashSet<>();
		rolesSet.add(user.getRole());
		autresp.setRoles(rolesSet);
		return autresp;
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

	public Boolean checkEmailExists(String email) {

		return repository.existsByEmail(email);
	}

}
