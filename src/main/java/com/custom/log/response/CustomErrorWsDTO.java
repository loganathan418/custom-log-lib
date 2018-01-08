package com.custom.log.response;

import java.io.Serializable;


public class CustomErrorWsDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String code;

	private String cause;

	private String message;

	private String type;
	
	public CustomErrorWsDTO(String code, String cause, String message, String type) {
		this.code = code;
		this.cause = cause;
		this.message = message;
		this.type = type;
	}

	public CustomErrorWsDTO() {
		// empty 
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCause(final String cause) {
		this.cause = cause;
	}

	public String getCause() {
		return cause;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}