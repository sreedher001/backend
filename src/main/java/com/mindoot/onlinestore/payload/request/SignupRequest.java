package com.mindoot.onlinestore.payload.request;

import java.util.Set;

import com.mindoot.onlinestore.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

	private String username;
	private String email;
	private String password;
	private Set<String> role;
}
