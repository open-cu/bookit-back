package com.opencu.bookit.adapter.out.security.spring.config;

import com.opencu.bookit.adapter.out.security.spring.jwt.AuthTokenFilter;
import com.opencu.bookit.adapter.out.security.spring.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.opencu.bookit.adapter.out.security.spring.jwt.AuthEntryPointJwt;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsServiceImpl;


/**
 * Spring Security Configuration. Defines security settings, filters and access rules
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthEntryPointJwt unauthorizedHandler;

    private final SecurityService securityService;

    public WebSecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            AuthEntryPointJwt unauthorizedHandler,
            SecurityService securityService
    ) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.securityService = securityService;
    }


    /**
     * @return JWT authentication filter
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Creates and returns a DaoAuthenticationProvider bean configured to integrate
     * with the application's UserDetailsService and PasswordEncoder.
     * <p>
     *     The DaoAuthenticationProvider is a standard Spring Security authentication provider
     *     that retrieves user details from the configured UserDetailsService and uses the
     *     PasswordEncoder to verify passwords during authentication.
     * <p>
     * By exposing this as a Spring Bean, it can be plugged into the authentication manager
     * to handle user authentication based on database-backed user information.
     * @return a fully configured DaoAuthenticationProvider bean for authenticating users
     **/
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    /**
     * Creates and returns the AuthenticationManager bean used to authenticate users.
     * <p>
     *     This method obtains the AuthenticationManager instance from the given AuthenticationConfiguration,
     *     which is a central interface that holds the configuration for authentication in Spring Security.
     *     The AuthenticationManager is responsible for processing authentication requests,
     *     verifying user credentials, and establishing the security context.
     * <p>
     * By exposing AuthenticationManager as a Spring Bean, it can be injected into other components
     * (e.g., authentication filters or services) that require authentication capability.
     * @param authConfig the AuthenticationConfiguration containing the authentication setup
     * @return the AuthenticationManager used by the application for authenticating users
     * @throws Exception if an error occurs while retrieving the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * @return An encoder that defines password hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configures and returns the security filter chain bean that defines the HTTP security rules for the application.
     * <p>
     *     This method customizes Spring Security's HttpSecurity object to:
     *     Disable CSRF protection (commonly disabled for stateless APIs)
     *     Set a custom authentication entry point to handle unauthorized access attempts
     *     Configure session management to be stateless, suitable for token-based authentication
     *     Define authorization rules for different HTTP request patterns (not fully shown here)
     * <p>
     * The returned SecurityFilterChain bean is used by Spring Security to process incoming requests
     * according to the configured security policies.
     * @param http the HttpSecurity instance to configure restrictions and behaviors on HTTP requests
     * @return a configured SecurityFilterChain for the application's security processing
     * @throws Exception if an error occurs during configuration
     **/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    if (securityService.isDev()) {
                        auth.requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/h2-console/**",
                            "/togglz-console/**").permitAll();
                    }
                    else {
                        auth.requestMatchers("/togglz-console/**").hasAnyAuthority(SecurityService.getAdmin());
                    }
                    auth.requestMatchers(
                        "/api/v1/public/**",
                        "/auth/**",
                        "/api/v1/auth/telegram"
                        ).permitAll()
                        .anyRequest().authenticated();
                });

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
