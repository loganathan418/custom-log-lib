package com.custom.log.util;

import java.util.Map;
import java.util.Stack;

import org.slf4j.MDC;

import com.custom.log.constants.ApplicationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomMdcUtil {
	private static final ThreadLocal<Stack<Map<String, String>>> keyInfos =
			new ThreadLocal<Stack<Map<String, String>>>() {
		@Override protected Stack<Map<String, String>> initialValue() {
			return new Stack<>();
		}
	};

	/**
	 * Setter to add KeyInfo to stack.
	 * @param keyInfo
	 */
	public static void setKeyInfo(Map<String, String> keyInfo) {
		keyInfos.get().push(keyInfo);
	}

	public static void getKeyInfo() {
		if (!keyInfos.get().isEmpty()) {
			Map<String, String> keyInfo = keyInfos.get().pop();
			if (null != keyInfo) {
				MDC.put(ApplicationConstants.KEY_INFO, keyInfo.toString());
			} 
		}
	}

	/**
	 * Override toString Request Dto.
	 * @param request
	 */
	private static void setRequest(Object request) {
		if (null != request) {
			MDC.put(ApplicationConstants.REQUEST, request.toString());
		}
	}

	/**
	 * Override toString Response Dto.
	 * @param response
	 */
	public static void setResponse(Object response) {
		if (null != response) {
			MDC.put(ApplicationConstants.RESPONSE, response.toString());
		}
	}


	/**
	 * For Storing the request temporarily 
	 * * @param request
	 * @deprecated set TdpLog.logRequest to set request during Error.
	 */
	@Deprecated
	public static void setTempRequest(Object request) {
		if (null != request) {
			MDC.put(ApplicationConstants.TEMP_REQUEST, jsonPrint(request, request.getClass()));
		}
	}

	/**
	 * For getting the temp request.
	 * * @param request
	 */
	public static String getTempRequest() {
		return MDC.get(ApplicationConstants.TEMP_REQUEST);
	}

	/**
	 * Process Request in case of Error.
	 */
	public static void processRequest() {
		setRequest(getTempRequest());
	}

	/**
	 * Convert the Object to JSON.
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static String jsonPrint(final Object object, final Class clazz) {
		final ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = "";
		try {
			final Object jsonObj = objectMapper.readValue(objectMapper.writeValueAsString(object), clazz);
			jsonStr = objectMapper.writeValueAsString(jsonObj);
		} catch (final Exception exception) {
		}
		return jsonStr;
	}

	/**
	 * ErrorCode and ErrorMsg setter.
	 * @param code
	 * @param message
	 */
	public static void setErrorParam(String code, String message) {
		MDC.put(ApplicationConstants.ERROR_CODE, code);
		MDC.put(ApplicationConstants.ERROR_MSG, message);
	}

	/**
	 * Override serviceName during error Scenario from TdpExceptionHandler.
	 * @param serviceName
	 */
	public static void setServiceName(String serviceName) {
		MDC.put(ApplicationConstants.SERVICE_NAME, serviceName);
	}
	
	/**
	 * Set Service Url from Client Service.
	 * @param serviceUrl
	 */
	public static void setRemoteAddr(String serviceUrl) {
		if (null != serviceUrl) {
			MDC.put(ApplicationConstants.REMOTE_ADDR, serviceUrl);
		}
	}
}
