package com.wext.authservice.service;

import com.wext.authservice.client.ManagerService;
import com.wext.common.domain.ManagerDTO;
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
public class ManagerDetailsService implements UserDetailsService {

    @Autowired
    private ManagerService managerService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ManagerDTO user = managerService.getManagerById(Long.parseLong(s));
        List<String> roleList = Collections.singletonList(user.getRole());

        List<GrantedAuthority> authorities = roleList.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails
                .User(s, user.getPassword(), authorities);
    }
}
