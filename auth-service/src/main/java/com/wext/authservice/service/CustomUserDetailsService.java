package com.wext.authservice.service;

import com.wext.authservice.client.UserService;
import com.wext.common.domain.UserDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserDetail(username);
    }

    private UserDetails getUserDetail(@NonNull String userArg) {

        UserDTO user = this.userService.getUserAutoChoose(userArg);
        List<String> roleList = Collections.singletonList("USER");

        List<GrantedAuthority> authorities = roleList.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails
                .User(userArg, user.getPassword(), authorities);
    }
}
