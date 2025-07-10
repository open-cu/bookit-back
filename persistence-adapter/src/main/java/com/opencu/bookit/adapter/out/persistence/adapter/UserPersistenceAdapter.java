package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.UserMapper;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.SaveUserPort;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserModel findByName(String name) {
        var userEntity = userRepository.findByName(name);
        return userMapper.toModel(userEntity);
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toModel);
    }

    @Override
    public Optional<UserModel> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toModel);
    }

    @Override
    public UserModel save(UserModel userModel) {
        var userEntity = userMapper.toEntity(userModel);
        var savedEntity = userRepository.save(userEntity);
        return userMapper.toModel(savedEntity);
    }
}

