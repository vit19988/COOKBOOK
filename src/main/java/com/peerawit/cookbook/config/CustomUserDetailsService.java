package com.peerawit.cookbook.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peerawit.cookbook.entity.Role;
import com.peerawit.cookbook.entity.User;
import com.peerawit.cookbook.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User loadedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(
						"User not found with email: " + email));

		return new org.springframework.security.core.userdetails.User(loadedUser.getEmail(),
				loadedUser.getPassword(), getGrantedAuthorities(loadedUser.getRoles()));
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
		return roles.stream().map(Role::getName).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

}
