package ru.tbank.bookit.book_it_backend.security.jwt;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import ru.tbank.bookit.book_it_backend.model.User;

/**
 * Фильтр для проверки JWT токена в каждом запросе
 * Наследуется от OncePerRequestFilter, чтобы гарантировать однократное выполнение для каждого запроса
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class );

    /**
     * Основной метод фильтра, выполняющийся для каждого HTTP запроса
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Извлекаем JWT токен из заголовка Authorization
            String jwt = parseJwt(request);

            // Если токен существует и валиден
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Извлекаем имя пользователя из токена
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Загружаем данные пользователя
                User userDetails = (User) userDetailsService.loadUserByUsername(username);

                // Создаем объект аутентификации
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Добавляем детали запроса к аутентификации
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем аутентификацию в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}