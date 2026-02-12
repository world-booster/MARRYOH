package com.grape.module.user.security;

import com.grape.module.user.model.UserDto;
import com.grape.module.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException{
        UserDto user = userRepository.selectUserByKey(userKey)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userKey));

        return new CustomUserDetails(user);

    }
}
