package com.peerawit.cookbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CookbookApiException extends RuntimeException {
	
	private HttpStatus status;
	private String message;
	
	
	public CookbookApiException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
	
	

}
