package com.peerawit.cookbook.jwt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.peerawit.cookbook.exception.CookbookApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt-issuer}")
	private String jwtIssuer;

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds}")
	private int jwtExpirationInMs;

	/*
	 * @Autowired private UnAuthorizedResponse unAuthorizedResponse;
	 */

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	// generate token
	public String generateToken(Authentication authentication) {
		List<GrantedAuthority> grantAuthorities = new ArrayList<>(authentication.getAuthorities());
		List<String> roles = grantAuthorities.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		String jwt = Jwts.builder().setIssuer(jwtIssuer).setSubject("JWT Token")
				.claim("username", authentication.getName()).claim("role", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirationInMs))
				.signWith(getKey()).compact();
		return jwt;
	}

	// get username from the token
	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token)
				.getBody();
		String username = (String) claims.get("username");
		return username;
	}

	// validate JWT token
	public boolean validateToken(String token, HttpServletRequest request) {

		try {

			SecretKey key = Keys // generate key using the secret
					.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			throw new CookbookApiException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
		}

	}

	// get jwt Header
	public HttpHeaders getJwtTokenHeader(Authentication authentication) {

		String jwtToken = generateToken(authentication);
		HttpHeaders jwtHeader = new HttpHeaders();
		jwtHeader.setBearerAuth(jwtToken);

		return jwtHeader;
	}

}
