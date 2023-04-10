package com.peerawit.cookbook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peerawit.cookbook.config.CustomUserAuthenticationProvider;
import com.peerawit.cookbook.config.CustomUserDetailsService;
import com.peerawit.cookbook.entity.User;
import com.peerawit.cookbook.jwt.JwtTokenProvider;
import com.peerawit.cookbook.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserAuthenticationProvider customUserAuthenticationProvider;

	@Autowired
	private CustomUserDetailsService customUserDetailService;
	
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@PostMapping("/signup")
	public ResponseEntity<String> userSignup(@RequestBody User user) throws Exception {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new Exception("Email already exists");
		}
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/signin")
	public ResponseEntity<User> userSignin(@RequestBody User user) {
		System.out.println("sign in-------------" + user.toString());
		
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),
				user.getPassword());

		authentication = customUserAuthenticationProvider.authenticate(authentication);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		System.out.println("authentication getname "+authentication.getAuthorities());
		HttpHeaders jwtHeader = jwtTokenProvider.getJwtTokenHeader(authentication);
		User loginUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()-> new RuntimeException("----No---"));
		return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
	}

}
