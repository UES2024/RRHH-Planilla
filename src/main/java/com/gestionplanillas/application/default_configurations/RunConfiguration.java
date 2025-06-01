package com.gestionplanillas.application.default_configurations;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gestionplanillas.application.security.AuthenticatedUser;
import com.gestionplanillas.application.data.User;
import com.gestionplanillas.application.repository.UserRepository;
import com.gestionplanillas.application.services.UserService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class RunConfiguration implements ApplicationRunner {

    private final UserRepository  userRepository;
    private final UserService userService;
    private final ApplicationContext applicationContext;
    

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!(this.userRepository.findByUsername("admin").isPresent())){
            User adminUser = new User();
            Set<String>roles = new HashSet();
            roles.add("ROLE_ADMIN");
            adminUser.setHashedPassword("admin123");
            adminUser.setUsername("admin");
            adminUser.setRoles(roles);
            this.userService.save(adminUser);
        }
        System.out.println("El valor de la clase que estamos monitoreando");
        System.out.println(applicationContext.getBean(AuthenticatedUser.class));
    }
    
}
