package com.mindoot.onlinestore.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author Chandhru
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MailDto {

	private String from;

	private String sendToAddress;
	
	private String userName;

	private String subject;

	private String templateName;

	private Object body;

	private String cc;

	private String bccEmailList;

}
