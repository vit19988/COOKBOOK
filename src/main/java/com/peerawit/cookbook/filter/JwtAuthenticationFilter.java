package com.peerawit.cookbook.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.peerawit.cookbook.config.CustomUserDetailsService;
import com.peerawit.cookbook.jwt.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

//	@Autowired
//	private UnAuthorizedResponse unAuthorizedResponse;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// get JWT token from HTTP request
		String token = getJWTfromRequest(request);
		System.out.println("yes2");
		if (token != null) {
			System.out.println("yes");
			// validate token
			if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {

				// get username from token
				String username = jwtTokenProvider.getUsernameFromJWT(token);

				// load userDetails from the username
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
				
				
				System.out.println("Authority is---"+userDetails.getAuthorities());
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails.getUsername(), null, userDetails.getAuthorities());
				// set Spring security
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} else {
//			AuthResponseUtil.setResponseDataToUnAuthorizationBean("JWT token not found", request,
//					unAuthorizedResponse);
		}

		filterChain.doFilter(request, response);

	}

	// Bearer <accessToken>
	private String getJWTfromRequest(HttpServletRequest request) {
		System.out.println("GETTTTTTT");
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		

		return null;
	}

}
