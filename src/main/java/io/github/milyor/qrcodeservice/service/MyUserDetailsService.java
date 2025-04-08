package io.github.milyor.qrcodeservice.service;

import io.github.milyor.qrcodeservice.entity.UserPrincipal;
import io.github.milyor.qrcodeservice.entity.Users;
import io.github.milyor.qrcodeservice.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService  implements UserDetailsService {

    private final UserRepo repo;

    @Autowired
    public MyUserDetailsService(UserRepo repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserPrincipal(user);
    }


}
