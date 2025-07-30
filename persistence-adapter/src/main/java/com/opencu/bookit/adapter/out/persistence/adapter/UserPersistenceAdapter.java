package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.UserMapper;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.application.port.out.user.DeleteUserPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.SaveUserPort;
import com.opencu.bookit.application.port.out.user.UserPreferencesPort;
import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements
        LoadUserPort, SaveUserPort, DeleteUserPort, UserPreferencesPort {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<UserModel> findByName(String name) {
        var userEntity = userRepository.findByName(name);
        return userEntity.map(userMapper::toModel);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toModel);
    }

    @Override
    public Optional<UserModel> findById(UUID id) {
        return userRepository.findById(id).map(userMapper::toModel);
    }

    @Override
    public Optional<UserModel> findByTgId(Long tgId) {
        return userRepository.findByTgId(tgId).map(userMapper::toModel);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public Page<UserModel> findWithFilters(String email, String phone, Set<String> role, String search, Pageable pageable) {
        Specification<UserEntity> spec = Specification.where(null);

        Set<Role> roles = new HashSet<>();
        if (role != null) {
            for (String roleStr : role) {
                roles.add(Role.fromString(roleStr));
            }
        }
        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.equal(cb.lower(root.get("email")), email.toLowerCase())
            );
        }
        if (phone != null &&  !phone.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("phone"), "%" + phone + "%")
            );
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("username")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("firstName")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("lastName")), "%" + search.toLowerCase() + "%")
                )
            );
        }
        if (!roles.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("roles").in(roles));
        }
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toModel);
    }

    @Override
    public UserModel save(UserModel userModel) {
        var userEntity = userMapper.toEntity(userModel);
        var savedEntity = userRepository.save(userEntity);
        return userMapper.toModel(savedEntity);
    }

    @Override
    public void deleteById(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isSubscribedToNotifications(UUID userId) {
        return userRepository.findById(userId)
                .map(UserEntity::isSubscribedToNotifications)
                .orElse(false);
    }

    @Override
    public void setNotificationPreference(UUID userId, boolean subscribed) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setSubscribedToNotifications(subscribed);
            userRepository.save(user);
        });
    }
}

