package com.nseindia.api.apigateway.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.nseindia.api.apigateway.security.service.SecConst;

import io.jsonwebtoken.Jwts;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter{
	
	private static final Logger LOGGER=LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try{
			LOGGER.info("Inside doFilter");
			String Header=request.getHeader("AuthHeader");
			LOGGER.info("Header "+Header);
			if (Header == null || !Header.startsWith("Auth")) {
				LOGGER.error("Auth header not present ");
				if(request.getRequestURI().equals("/test") || request.getRequestURI().equals("/auth")){
					chain.doFilter(request, response);
				}else{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authentication header not present");
				}
				
			} else {
				SecurityContextHolder.getContext().setAuthentication(getUser(Header));
				chain.doFilter(request, response);
			}
			
		} catch (Exception ex) {
			LOGGER.error("Invalid token exception : "+ex.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Authentication Token");

		}
	}

	private UsernamePasswordAuthenticationToken getUser(String header) {
		LOGGER.info("Validating Token");
		
			String user=Jwts.parser().setSigningKey(SecConst.secret.getBytes()).parseClaimsJws(header.replace("Auth ", "")).getBody().getSubject();
			if(user!=null && (user.equals("userName1") || user.equals("userName2")))
			{
				LOGGER.info("Token is valid");
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			
		
		return null;
	}
	
	/*public void setUnauthorizedResponse(HttpServletResponse response) {
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    response.setContentType("application/json");
	    //Response unAuthorizedResponse = Response.unauthorized().build();
	    try {
	        PrintWriter out = response.getWriter();
	        //out.println(unAuthorizedResponse.toJsonString());
	        out.println("Invalid Token from setUnauthorizedResponse");
	    } catch (IOException e) {
	    	LOGGER.error("Exception in setUnauthorizedResponse");
	    }
	}*/
	

}
