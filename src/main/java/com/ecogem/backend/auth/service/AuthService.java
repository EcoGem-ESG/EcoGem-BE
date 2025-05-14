package com.ecogem.backend.auth.service;


import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.Status;
import com.ecogem.backend.auth.repositorty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    //  Signin
    public User signup(String loginId, String pwd, String email, Role role) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new IllegalArgumentException("The ID is already in use.");
        }

        User user = User.builder()
                .loginId(loginId)
                .pwd(pwd)
                .email(email)
                .role(role)                        // Company/Store differenation
                .status(Status.INCOMPLETE)         // Not Registered yet
                .build();

        return userRepository.save(user);
    }

    // Log in
    public User login(String loginId, String pwd) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("No matching Users."));

        if (!user.getPwd().equals(pwd)) {
            throw new IllegalArgumentException("Incorrect Password.");
        }

        return user;
    }
}
