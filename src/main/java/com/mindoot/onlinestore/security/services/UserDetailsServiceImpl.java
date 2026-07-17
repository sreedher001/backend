package com.mindoot.onlinestore.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

//	@Override
//	@Transactional
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//
//		return UserDetailsImpl.build(user);
//	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

		return UserDetailsImpl.build(user);
	}

	
//	public UserDetails loadUserById(Long id) {
//
//	    User user = userRepository.findById(id)
//	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//	    return UserDetailsImpl.build(user);
//	}
	
	
	public UserDetails loadUserById(Long id) {

	    User user = userRepository.findByIdWithRoles(id)
	            .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

	    return UserDetailsImpl.build(user);
	}
}
