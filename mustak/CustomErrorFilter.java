package com.nseindia.api.apigateway.security.filter;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nseindia.api.apigateway.security.model.ErrorDetails;



public class CustomErrorFilter extends ZuulFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorFilter.class);
	
    @Override
    public String filterType() {
    	//LOGGER.info("Inside CustomErrorFilter filterType() ");
    	return "post";
    }

    @Override
    public int filterOrder() {
    	//LOGGER.info("Inside CustomErrorFilter filterOrder() ");
        return -1; // Needs to run before SendErrorFilter which has filterOrder == 0
    }

    @Override
    public boolean shouldFilter() {
        
    	//LOGGER.info("Inside CustomErrorFilter shouldFilter() ");
    	RequestContext ctx = RequestContext.getCurrentContext();
        if(ctx !=null && ctx.getResponseStatusCode() == 500)
        	return true;
        return false;
    	
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
           
           
            if (ctx != null && ctx.getResponseStatusCode() == 500) {
               
               LOGGER.error("Error from service detected: "+ctx.getResponseBody());
               ctx.setResponseBody(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorDetails(new Date(),"500","Internal Server Error","Something went wrong",ctx.getRequest().getRequestURI().toString()))); //Custom Error Message
               ctx.getResponse().setContentType("application/json");
               ctx.setResponseStatusCode(500); 
              
            }
        }
        catch (Exception ex) {
            LOGGER.error("Exception filtering in custom error filter", ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }
    
    
    
}