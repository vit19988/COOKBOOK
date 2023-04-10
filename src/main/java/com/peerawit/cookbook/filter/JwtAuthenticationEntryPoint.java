package com.peerawit.cookbook.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.peerawit.cookbook.payload.ErrorDetails;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Access Denied !",
				"You need to log in to access this page");

		ObjectWriter ow = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.writer().withDefaultPrettyPrinter();

		String json = ow.writeValueAsString(errorDetails);

		response.setContentType(MediaType.APPLICATION_JSON.toString());
		response.getWriter().write(json);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
	}

}
