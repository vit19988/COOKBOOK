package com.peerawit.cookbook.config;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.peerawit.cookbook.filter.JwtAuthenticationEntryPoint;
import com.peerawit.cookbook.filter.JwtAuthenticationFilter;


@Configuration
public class SecurityConfig {

	@Autowired
	private MyAccessDeniedHandler myAccessDeniedHandler;
	
	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public AuthenticationEventPublisher authenticationEventPublisher(
			ApplicationEventPublisher appEventPublisher) {
		System.out.println("XXXXXXXXXXXXXXXXXXXX");
		return new DefaultAuthenticationEventPublisher(appEventPublisher);
	}

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

		CorsConfigurationSource corsConfigSource = new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration config = new CorsConfiguration();
				config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
				config.setAllowedMethods(Collections.singletonList("*"));
				config.setAllowCredentials(true);
				config.setAllowedHeaders(Collections.singletonList("*"));
				config.setMaxAge(3600L);
				config.setExposedHeaders(Collections.singletonList("*"));
				return config;
			}

		};

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// Please do not generate the JSESSIONID
				.and().cors().configurationSource(corsConfigSource).and().csrf().disable()
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
				.addFilterBefore(jwtAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/recipes").hasRole("ADMIN")
				.anyRequest()
				.authenticated().and().formLogin().and().httpBasic()
				.and().exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(myAccessDeniedHandler);

		return http.build();

	}

}
