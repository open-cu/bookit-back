package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.application.port.out.user.LoadUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    LoadUserPort loadUserPort;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String tgIdString) throws UsernameNotFoundException {
        Long tgId;
        try {
            tgId = Long.valueOf(tgIdString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid tgId: " + tgIdString);
        }
        return loadUserPort.findByTgId(tgId)
                           .map(UserDetailsImpl::build)
                           .orElseThrow(() -> new UsernameNotFoundException("The user was not found " + tgId));
    }
}
