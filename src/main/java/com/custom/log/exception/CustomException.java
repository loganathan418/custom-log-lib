package com.custom.log.exception;

import java.util.ArrayList;
import java.util.List;

import com.custom.log.response.CustomErrorWsDTO;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code;

	private String message;

	private String type;

	private List<CustomErrorWsDTO> errors;

	private StackTraceElement[] tdpStackTrace;

	/**
	 * default constructor.
	 */
	public CustomException() {
		super();
	}

	public CustomException(final String code) {
		super();
		this.code = code;
		this.tdpStackTrace = this.getStackTrace();
	}
	
	public CustomException(final Throwable cause) {
		super(cause);
		this.tdpStackTrace = this.getStackTrace();
	}
	
	public CustomException(final String code, final Throwable cause) {
		super();
		this.code = code;
		this.tdpStackTrace = cause != null ? cause.getStackTrace() : this.getStackTrace();
	}
	
	public CustomException(final String code, final String type, final Throwable cause) {
		super();
		this.code = code;
		this.type = type;
		this.tdpStackTrace = cause != null ? cause.getStackTrace() : this.getStackTrace();
	}
	
	public CustomException(final String code, final String message, final String type) {
		super();
		this.code = code;
		this.message = message;
		this.type = type;
		this.tdpStackTrace = this.getStackTrace();
	}

	public CustomException(final String code, final String message, final String type, final Throwable cause) {
		super();
		this.code = code;
		this.message = message;
		this.type = type;
		this.tdpStackTrace = cause != null ? cause.getStackTrace() : this.getStackTrace();
	}
	
	public CustomException(final List<CustomErrorWsDTO> errors) {
		super();
		this.errors = errors;
		this.tdpStackTrace = this.getStackTrace();
	}
	
	public CustomException(final List<CustomErrorWsDTO> errors, final Throwable cause) {
		super();
		this.errors = errors;
		this.tdpStackTrace = cause != null ? cause.getStackTrace() : this.getStackTrace();
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the errors
	 */
	public List<CustomErrorWsDTO> getErrors() {
		if (null == errors) {
			return new ArrayList<>();
		}
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<CustomErrorWsDTO> errors) {
		this.errors = errors;
	}

	/**
	 * get tdp stackTrace.
	 * 
	 * @return tdp stackTrace.
	 */
	public StackTraceElement[] getTdpStackTrace() {
		return tdpStackTrace;
	}

	/**
	 * set stackTrace
	 * 
	 * @param tdpStackTrace
	 */
	public void setTdpStackTrace(StackTraceElement[] tdpStackTrace) {
		this.tdpStackTrace = tdpStackTrace;
	}
}
