package com.peerawit.cookbook.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.context.request.WebRequest;

import com.peerawit.cookbook.payload.ErrorDetails;


@ControllerAdvice
public class GlobalExceptionHandler {

	// handle specific exceptions
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
			ResourceNotFoundException ex, WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(CookbookApiException.class)
	public ResponseEntity<ErrorDetails> handleCookbookApiException(
			ResourceNotFoundException ex, WebRequest webRequest) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(Forbidden.class)
	public ResponseEntity<ErrorDetails> handleBlogApiException(Forbidden exception,
			WebRequest webRequest) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorDetails> handleDataIntregityException(
			DataIntegrityViolationException exception, WebRequest webRequest) {
		String message = exception.getRootCause().getMessage();
		message = message.substring(message.indexOf("Detail") + 8,message.length());
		ErrorDetails errorDetails = new ErrorDetails(new Date(), message,
				webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidExceptionException(
			MethodArgumentNotValidException exception, WebRequest webRequest) {
		Map<String, String> errors = new HashMap<>();
		exception.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		ErrorDetails errorDetails = new ErrorDetails(new Date(),
				errors.toString().replace("{", "").replace("}", "").replace("=", ":"),
				webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorDetails> lockedException() {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), "ACCOUNT_LOCKED",
				"Your account has been lokced, please contact the administrstor");
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest webRequest) {
		
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
