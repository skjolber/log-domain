package com.github.skjolber.log.domain.example.spring.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.github.skjolber.log.domain.utils.DomainMarker;

@Component
public class LogInterceptor implements HandlerInterceptor {

	private static final Pattern pattern = Pattern.compile("\\{(?<key>[a-zA-Z0-9]+)\\}");

	private static Logger log = org.slf4j.LoggerFactory.getLogger(LogInterceptor.class);
	
	private ThreadLocal<DomainMarker> threadLocal = new ThreadLocal<>();

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
		log.info("Request Completed!");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model)
			throws Exception {
		log.info("Method executed");

		DomainMarker domainMarker = threadLocal.get();
		if(domainMarker != null) {
			domainMarker.popContext();
			
			threadLocal.remove();
		}

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		log.info("Before process request");
		
		mdc:
		if(object instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)object;
			
			Logged annotation = handlerMethod.getMethod().getAnnotation(Logged.class);
			if(annotation == null) {
				annotation = handlerMethod.getBeanType().getAnnotation(Logged.class);
			}
			
			if(annotation != null) {
				Class<? extends DomainMarker> value = annotation.value();
				
				RequestMapping methodRequestMapping = handlerMethod.getMethod().getAnnotation(RequestMapping.class);
				if(methodRequestMapping != null) {

					String[] requestMappingValue = methodRequestMapping.value();
					if(requestMappingValue.length == 1) {
						DomainMarker marker;
			        	try {
							marker = value.newInstance();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
			        	
			        	// slash the controller path, if present
			        	String requestURI = request.getRequestURI();
						RequestMapping classRequestMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
						if(classRequestMapping != null) {
							String[] classValues = classRequestMapping.value();
							if(classValues.length == 1) {
								requestURI = requestURI.substring(classValues[0].length());
							} else {
								break mdc;
							}
						}
						
						String values[] = requestURI.split("/");
			        	String keys[] = requestMappingValue[0].split("/"); // assume /a/b/c
			        	if(values.length == keys.length) {
				        	for(int i = 0; i < keys.length; i++) {
				        		// Now create matcher object.
				                Matcher m = pattern.matcher(keys[i]);
					            if(m.matches()) {
					            	String key = m.group("key");
					            	if(marker.definesKey(key)) {
					            		marker.parseAndSetKey(key, values[i]);
					            	}
					            }
				        	}
				        	
				        	marker.pushContext();
				        	
				        	threadLocal.set(marker);
				        	
				        	return true;
			        	}
					}
				}
			}
		}
		threadLocal.remove();
		
		return true;
	}

}