package com.opencu.bookit.adapter.out.security.spring.jwt;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

 /**
 *
 * A Spring Security filter that intercepts each HTTP request once and processes JWT authentication.
 *
 * <p>
     * This filter extracts the JWT token from the Authorization header of the incoming request,
     * validates the token, extracts the Telegram user ID (tgId) from the token,
     * and if valid, sets the corresponding authenticated user in the SecurityContext.
 * <p>
     * It ensures that each request is authenticated based on the provided JWT,
     * enabling stateless authentication in the application.
 * <p>
 * If authentication cannot be set due to token errors or missing data, the filter logs the error but
 * continues filter chain execution without authenticating the user.
 **/

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

     /**
      * Filters each HTTP request to authenticate user by JWT token.
      *
      * @param request current HTTP request
      * @param response current HTTP response
      * @param filterChain filter chain for invoking next filters
      * @throws ServletException in case of servlet errors
      * @throws IOException in case of IO errors
      **/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                Long tgId = jwtUtils.getTgIdFromJwtToken(jwt);

                if (tgId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(tgId));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

     /**
      * Parses the JWT token from the Authorization header of the HTTP request.
      *
      * @param request the HTTP request
      * @return the JWT token string if present and well-formed, otherwise null
      * */
    private String parseJwt(HttpServletRequest request) {
        final int CHARS_TO_SKIP_PREFIX_BEARER = "Bearer ".length();
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(CHARS_TO_SKIP_PREFIX_BEARER);
        }
        return null;
    }
}