package com.voicera.authController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.config.LogoutService;
import com.voicera.user.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

  private final AuthenticationService service;
  private final LogoutService logoutService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
	  System.out.println(request);
   AuthenticationResponse authresp = service.authenticate(request);
	return new ResponseEntity<>(authresp, HttpStatus.OK);

  }


  
  @PostMapping("/validate-email")
  public Boolean validateEmail(@RequestBody User user) {
	  if(user.getEmail()!=null) {
    return service.checkEmailExists(user.getEmail());
	  }else {
		  return false;
	  }
      
   
  }
  
  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    logoutService.logout(request, response, null);
  }



}
