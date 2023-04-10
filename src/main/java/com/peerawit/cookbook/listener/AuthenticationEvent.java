package com.peerawit.cookbook.listener;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.peerawit.cookbook.service.LoginAttemptService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationEvent {

	@Autowired
	private LoginAttemptService logingAttemptService;

	@EventListener
	public void onSuccess(AuthenticationSuccessEvent event) {
		
		log.warn("onSuccess");
		
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			logingAttemptService.evictUserFromLoginAttemptCache(username);
		}
	}

	@EventListener
	public void onFailure(AbstractAuthenticationFailureEvent event) {
		
		log.warn("onFailure");
		
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof String) {
			System.out.println("INNNNNNNNN");
			String username = (String) event.getAuthentication().getPrincipal();
			logingAttemptService.addUserToLoginAttemptCache(username);
			int attempt = logingAttemptService.getUserxAttempts(username);
			System.out.println("Attempts is---"+attempt);
		}
	}
}