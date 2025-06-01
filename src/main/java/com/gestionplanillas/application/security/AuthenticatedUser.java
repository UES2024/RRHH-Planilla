package com.gestionplanillas.application.security;

import com.gestionplanillas.application.data.User;
import com.gestionplanillas.application.repository.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
