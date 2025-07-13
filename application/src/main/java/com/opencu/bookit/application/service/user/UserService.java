package com.opencu.bookit.application.service.user;

import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.SaveUserPort;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    @Autowired
    public UserService(LoadUserPort loadUserPort,
                       SaveUserPort saveUserPort,
                       LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    public Optional<UserModel> findById(UUID id) {
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
        return saveUserPort.save(userModel);
    }
}
