package com.custom.log;

import java.util.Stack;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.custom.log.constants.ApplicationConstants;
import com.custom.log.util.CustomMdcUtil;

@Aspect
@Component
public class CustomLogAspect {
	private static final Logger LOG = LoggerFactory.getLogger(CustomLogAspect.class);

	private static final ThreadLocal<Stack<Long>> startTimes =
			new ThreadLocal<Stack<Long>>() {
		@Override protected Stack<Long> initialValue() {
			return new Stack<>();
		}
	};

	@Around("@annotation(CustomLog)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		startTimes.get().push(System.currentTimeMillis());
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		CustomLog customLog = signature.getMethod().getAnnotation(CustomLog.class);
		if (customLog!=null && customLog.logRequest()) {
				Object[] args = joinPoint.getArgs();
				StringBuilder request = new StringBuilder();
				for (Object arg : args) {
					request.append(CustomMdcUtil.jsonPrint(arg, arg.getClass()));
				}
				MDC.put(ApplicationConstants.TEMP_REQUEST, request.toString());
			}
		Object proceed = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - startTimes.get().pop();

		StringBuilder service = new StringBuilder();
		service.append(joinPoint.getTarget().getClass().getName()).append(".").append(signature.getName());

		String serviceName = customLog != null ? customLog.serviceName() : "";
		if (StringUtils.isEmpty(serviceName)) {
			serviceName = service.toString();
		}

		MDC.put(ApplicationConstants.SERVICE_NAME, serviceName);

		final StringBuilder logMessage = new StringBuilder();
		logMessage.append(service).append(" timing:" + executionTime);

		MDC.put(ApplicationConstants.EXECUTION_TIME, String.valueOf(executionTime));
		
		CustomMdcUtil.getKeyInfo();

		LOG.info(logMessage.toString());

		MDC.remove(ApplicationConstants.SERVICE_NAME);
		MDC.remove(ApplicationConstants.EXECUTION_TIME);
		MDC.remove(ApplicationConstants.KEY_INFO);
		MDC.remove(ApplicationConstants.REQUEST);
		MDC.remove(ApplicationConstants.RESPONSE);
		MDC.remove(ApplicationConstants.ERROR_CODE);
		MDC.remove(ApplicationConstants.ERROR_MSG);

		return proceed;
	}
}