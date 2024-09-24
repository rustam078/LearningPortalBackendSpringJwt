package com.voicera.authController;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.token.Token;
import com.voicera.token.TokenRepository;
import com.voicera.user.User;

@RestController
public class SsoOauthController {
    private static final Logger logger = LoggerFactory.getLogger(SsoOauthController.class);

    @Autowired
	private  TokenRepository tokenRepository;
    @Autowired
    @Lazy
    private PasswordEncoder encoder;


@GetMapping("/userdtls/{token}")
public ResponseEntity<?> userToken(@PathVariable String token) {
	System.out.println("SsoOauthController.userToken() "+token);
	   var user = tokenRepository.findByToken(token);
      if(!user.isPresent()) {
    	  throw new RuntimeException("Invalid Credentials...");
      }
    	  Token tokendtls = user.get();
    	  if(tokendtls.isExpired()||tokendtls.isRevoked()) {
    		  throw new RuntimeException("Token  invaild..."); 
    	  }
    	  User userdtls = tokendtls.getUser();
       AuthenticationResponse autresp = new AuthenticationResponse();	
       BeanUtils.copyProperties(userdtls, autresp);
       autresp.setToken(token);
       logger.info("Returning authentication response for user: {}", user);
       return new ResponseEntity<>(autresp, HttpStatus.OK);
       
}

}
