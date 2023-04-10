package com.peerawit.cookbook.config;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peerawit.cookbook.entity.Role;
import com.peerawit.cookbook.entity.User;
import com.peerawit.cookbook.repository.UserRepository;
import com.peerawit.cookbook.service.LoginAttemptService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserAuthenticationProvider implements AuthenticationProvider {


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		String email = authentication.getPrincipal().toString();
		String pwd = authentication.getCredentials().toString();
		User loadedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Email not found"));

		if (!encoder.matches(pwd, loadedUser.getPassword())) {
			AuthenticationFailureBadCredentialsEvent customSpringEvent = new AuthenticationFailureBadCredentialsEvent(
					authentication, new BadCredentialsException("Invalid Password"));
			applicationEventPublisher.publishEvent(customSpringEvent);
			validateLoginAttempt(loadedUser);
			userRepository.save(loadedUser);
			throw new BadCredentialsException("Invalid Password!");
		}
		

		log.warn("Success login!");
		AuthenticationSuccessEvent customSpringEvent = new AuthenticationSuccessEvent(
				authentication);
		applicationEventPublisher.publishEvent(customSpringEvent);
		validateLoginAttempt(loadedUser);
		
		loadedUser.setLastLoginDate(new Date());
		userRepository.save(loadedUser);
		
		return new UsernamePasswordAuthenticationToken(loadedUser.getEmail(), pwd,
				getGrantedAuthorities(loadedUser.getRoles()));
	}

	private void validateLoginAttempt(User user) {
		System.out.println("---In here-----");
		System.out.println(user.getIsLocked());
		if (!user.getIsLocked()) {
			if (loginAttemptService.hasExceedMaxAttempts(user.getEmail())) {
				System.out.println("yes, exceed");
				user.setIsLocked(true);
			} else {
				System.out.println("---In here2-----");
				user.setIsLocked(false);
			}
		} else {
			throw new LockedException(null);
		}

	}

	private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
		return roles.stream().map(Role::getName).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
