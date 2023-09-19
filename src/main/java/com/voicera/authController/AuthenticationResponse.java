package com.voicera.authController;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voicera.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

	private String token;
	private Integer id;
	private String firstname;
	private String email;
	private Set<Role> roles;

  
}
