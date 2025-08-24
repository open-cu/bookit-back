package com.opencu.bookit.application.service.user;

import com.opencu.bookit.application.port.out.user.DeleteUserPort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.SaveUserPort;
import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final DeleteUserPort deleteUserPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Autowired
    public UserService(LoadUserPort loadUserPort,
                       SaveUserPort saveUserPort, DeleteUserPort deleteUserPort,
                       LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.deleteUserPort = deleteUserPort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    public Optional<UserModel> findById(UUID id) {
        log.info("Admin {} searched for user {}", loadAuthorizationInfoPort.getCurrentUser().getId(), id);
        if (loadUserPort.getSystemUser().getId().equals(id)) {
            return Optional.empty();
        }
        return loadUserPort.findById(id);
    }

    public Optional<UserModel> findByTgId(Long tgId) {
        return loadUserPort.findByTgId(tgId);
    }

    public UUID getTestUserId() {
        var user = loadUserPort.findByName("Alice Johnson");
        if (user.isEmpty()) {
            throw new NoSuchElementException("Test user not found");
        }
        return user.get().getId();
    }

    public Optional<UserModel> getUserById(UUID id) {
        return loadUserPort.findById(id);
    }

    @Transactional
    public UserModel updateProfile(String firstName, String lastName, String email, String phone) {
        UserModel userModel = loadAuthorizationInfoPort.getCurrentUser();
        if (firstName != null) userModel.setFirstName(firstName);
        if (lastName != null) userModel.setLastName(lastName);
        if (email != null) userModel.setEmail(email);
        if (phone != null) userModel.setPhone(phone);

        log.info("Updating user profile of user {}", userModel.getId());
        return saveUserPort.save(userModel);
    }

    @Transactional
    public UserModel patchUser(
            UUID userId,
            String firstName,
            String lastname,
            String email,
            List<String> roles,
            UserStatus userStatus
    ) {
        Optional<UserModel> userOpt = loadUserPort.findById(userId);
        if (userOpt.isEmpty())
        {
            throw new NoSuchElementException("No such user " + userId + " found");
        }
        UserModel user = userOpt.get();
        if (user.getRoles().contains(Role.ROLE_SYSTEM_USER)) {
            throw new IllegalStateException("System users cannot be patched");
        }
        if (firstName != null) user.setFirstName(firstName);
        if (lastname != null) user.setLastName(lastname);
        if (email != null) user.setEmail(email);
        if (roles != null && !roles.isEmpty()) {
            Set<Role> rolesSet = new HashSet<>();
            roles.forEach(role -> rolesSet.add(Role.valueOf(role)));
            user.setRoles(rolesSet);
        }
        if (userStatus != null) user.setStatus(userStatus);
        user.setUpdatedAt(LocalDateTime.now(zoneId));

        log.info("Admin {} patched user {}", loadAuthorizationInfoPort.getCurrentUser().getId() , userId);
        return saveUserPort.save(user);
    }

    public Page<UserModel> findWithFilters(String email, String phone, Set<String> role, String search, Pageable pageable) {
        log.info("Admin {} searched for users", loadAuthorizationInfoPort.getCurrentUser().getId());
        return loadUserPort.findWithFilters(email, phone, role, search, pageable);
    }

    public void deleteById(UUID userId) {
        if (loadUserPort.getSystemUser().getId().equals(userId)) {
            throw new IllegalStateException("System users cannot be deleted");
        }
        log.info("Admin {} deleted user {}", loadAuthorizationInfoPort.getCurrentUser().getId(), userId);
        deleteUserPort.deleteById(userId);
    }
}
