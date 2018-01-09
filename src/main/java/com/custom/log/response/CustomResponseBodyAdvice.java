package com.custom.log.response;

import static com.custom.log.constants.ApplicationConstants.RESPONSE_TYPE;
import static com.custom.log.constants.ApplicationConstants.FIELD;
import static com.custom.log.constants.ApplicationConstants.BASIC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomResponseBodyAdvice.class);
	
	@Resource
	private Environment env;
	private MapperFactory mapperFactory;
	private MapperFacade mapperFacade;
	private Method method;
	
	public CustomResponseBodyAdvice() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFacade = mapperFactory.getMapperFacade();
	}
	
	@Override
	public CustomResponseWsDTO<Object> beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType,
			Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest request, ServerHttpResponse response) {
		CustomResponseBody customResponseBody = methodParameter.getMethodAnnotation(CustomResponseBody.class);

		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		Enumeration<String> paramNames = servletRequest.getParameterNames();
		String responseType = "";
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (RESPONSE_TYPE.equalsIgnoreCase(paramName)) {
				responseType = servletRequest.getParameter(paramName);
			}
		}

		return generateResponseBody(customResponseBody.success(), responseType, customResponseBody.methodName(), object);
	}

	private CustomResponseWsDTO<Object> generateResponseBody(Boolean flag, String responseType, String methodName, Object object) {
		String partialResp = env.getProperty(methodName);
		if (!StringUtils.isEmpty(responseType) && responseType.equalsIgnoreCase(BASIC)
				&& !StringUtils.isEmpty(methodName) && !StringUtils.isEmpty(partialResp)) {
			return mapPartialResponse(object, partialResp, flag);
		}

		return new CustomResponseWsDTO<>(object, null, flag);
	}
	
	private CustomResponseWsDTO<Object> mapPartialResponse(Object object, String partialResp, Boolean flag) {
		ClassMapBuilder mapBuilder = mapperFactory.classMap(object.getClass(), object.getClass());
		try {
			method = mapBuilder.getClass().getMethod(FIELD, String.class,String.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			LOGGER.error("Error Mapping Partial Response: {}", e1.getMessage());
		}
		try {
			String[] str = partialResp.split(";");
			for (String property : str) {
				method.invoke(mapBuilder, property, property);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error Mapping Partial Response: {}", e.getMessage());
		}
		mapBuilder.register();

		Object partialResponse = mapperFacade.map(object, object.getClass());
		return new CustomResponseWsDTO<>(partialResponse, null, flag);
	}

	@Override
	public boolean supports(MethodParameter methodParamter, Class<? extends HttpMessageConverter<?>> clazz) {
		return methodParamter.getMethodAnnotation(CustomResponseBody.class) != null;
	}
}