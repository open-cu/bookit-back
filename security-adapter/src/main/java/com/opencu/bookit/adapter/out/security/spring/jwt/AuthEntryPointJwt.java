package com.opencu.bookit.adapter.out.security.spring.jwt;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * This class implements Spring Security's AuthenticationEntryPoint interface
 * to handle unauthorized access attempts in the application.
 * <p>
 *      When an unauthenticated user tries to access a secured resource,
 *      the commence method is invoked to commence an authentication scheme.
 * <p>
 *      In this implementation, the class logs the unauthorized access attempt along with
 *      the requested URI and the exception message, then sends a 401 Unauthorized HTTP response
 *      to the client indicating that authentication is required.
 * <p>
 * This class centralizes the handling of authentication errors and provides consistent
 * error responses for security exceptions related to unauthenticated requests.
 **/
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class );

    /**
     * Handles an unauthorized request by logging the error and sending
     * a 401 Unauthorized response to the client.
     *
     * @param request the HttpServletRequest that resulted in an AuthenticationException
     * @param response the HttpServletResponse to send the error response
     * @param authException the exception that caused the commencement
     * @throws IOException if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     **/
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error at {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}