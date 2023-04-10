package com.peerawit.cookbook.payload;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ErrorDetails {
	
	private Date timestamp;
	private String messsage;
	private String details;
	

}
