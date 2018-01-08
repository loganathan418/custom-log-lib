package com.custom.log.exception;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.custom.log.response.CustomErrorWsDTO;
import com.custom.log.response.CustomResponseWsDTO;
import com.custom.log.util.CustomMdcUtil;

@ControllerAdvice
public class CustomExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@Resource
	Environment environment;
	
	/**
	 * Intercepts Custom exception
	 * 
	 * @param exp
	 * @return CustomResponseWsDTO<Object>
	 */
	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.PARTIAL_CONTENT)
	@ResponseBody
	public CustomResponseWsDTO<Object> handleException(CustomException exp) {
		CustomMdcUtil.processRequest();
		StackTraceElement[] traceElements = exp.getTdpStackTrace();
		if (null != traceElements && traceElements.length > 0) {
			StackTraceElement traceElement = traceElements[0];
			if (null != traceElement) {
				CustomMdcUtil.setServiceName(traceElement.getMethodName());	
			}
		}
		
		CustomResponseWsDTO<Object> responseDTO = new CustomResponseWsDTO<>();
		if (!CollectionUtils.isEmpty(exp.getErrors())) {
			responseDTO.setErrors(exp.getErrors());
			CustomMdcUtil.setErrorParam(getErrorCode(exp.getErrors()), getErrorMessage(exp.getErrors()));
		} else {
			CustomErrorWsDTO error = new CustomErrorWsDTO();
			error.setMessage(StringUtils.isNotEmpty(exp.getMessage()) ? exp.getMessage()
					: environment.getProperty(exp.getCode()));
			error.setType(exp.getType());
			error.setCode(exp.getCode());
			List<CustomErrorWsDTO> errors = new ArrayList<>();
			errors.add(error);
			responseDTO.setErrors(errors);
			CustomMdcUtil.setErrorParam(getErrorCode(errors), getErrorMessage(errors));
		}

		LOGGER.error("Exception Occurred: {}, Stack Trace: {}", exp.getMessage(), traceElements);
		return responseDTO;
	}

	/**
	 * Intercepts any exception that is not covered by tdp and bind exception
	 *
	 * @param exp
	 * @return List<TdpErrorWsDTO>
	 */
	@ExceptionHandler(Exception.class) 
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public CustomResponseWsDTO<Object> handleException(Exception exp) {
		CustomMdcUtil.setErrorParam("", exp.getMessage());
		CustomMdcUtil.processRequest();
		CustomResponseWsDTO<Object> responseDTO = new CustomResponseWsDTO<>();
		LOGGER.error("Exception occured with exception : {}, error cause : {} and error message : {} ", exp,
				exp.getCause(), exp.getMessage());
		List<CustomErrorWsDTO> errors = new ArrayList<>();
		CustomErrorWsDTO error = new CustomErrorWsDTO();
		error.setCause(exp.getCause().toString());
		error.setMessage(exp.getMessage());
		errors.add(error);
		responseDTO.setErrors(errors);
		return responseDTO;
	}

	private String getErrorCode(List<CustomErrorWsDTO> errors) {
		List<String> errorCodes = new ArrayList<>();
		if (!CollectionUtils.isEmpty(errors)) {
			for (CustomErrorWsDTO errorDto : errors) {
				errorCodes.add(errorDto.getCode());
			}
		}
		
		return errorCodes.toString();
	}

	private String getErrorMessage(List<CustomErrorWsDTO> errors) {
		List<String> errorMsgs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(errors)) {
			for (CustomErrorWsDTO errorDto : errors) {
				errorMsgs.add(errorDto.getMessage());
			}
		}
		
		return errorMsgs.toString();
	}
}