package ru.tbank.bookit.book_it_backend.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String tgIdString) throws UsernameNotFoundException {
        Long tgId;
        try {
            tgId = Long.valueOf(tgIdString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid tgId: " + tgIdString);
        }
        return userRepository.findByTgId(tgId)
                .map(UserDetailsImpl::build)
                .orElseThrow(() -> new UsernameNotFoundException("The user was not found " + tgId));
    }
}
