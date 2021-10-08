package com.exp.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class EmailRequest {

	@Email
	@NotEmpty
	private String email;
	
	@NotEmpty
	private String subject;
	
	@NotEmpty
	private String body;
	
	@NotNull
	private LocalDateTime dateTime;
	
	@NotNull
	private ZoneId timeZone;
	
}
