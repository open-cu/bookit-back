package ru.tbank.bookit.book_it_backend.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

/**
 * Реализация UserDetailsService для загрузки пользовательских данных
 * Используется Spring Security для аутентификации пользователей
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    /**
     * Загружает пользователя по имени пользователя
     *
     * @param username имя пользователя
     * @return объект User, реализующий UserDetails
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }
}
