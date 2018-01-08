package com.custom.log.response;

import java.util.ArrayList;
import java.util.List;

public class CustomResponseWsDTO<T> {
	private boolean success;
	private T data;
	private List<CustomErrorWsDTO> errors;
	
	public CustomResponseWsDTO()
	{
		
	}
	
	public CustomResponseWsDTO(T data,List<CustomErrorWsDTO> errors,Boolean success)
	{
		this.data = data;
		this.errors = errors;
		this.success = success;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public List<CustomErrorWsDTO> getErrors() {
		if (errors == null) {
			return new ArrayList<>();
		}
		return errors;
	}
	
	public void setErrors(List<CustomErrorWsDTO> errors) {
		this.errors = errors;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
