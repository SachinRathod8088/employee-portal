package com.example.employee.service;

import com.example.employee.model.EmployeeLogin;
import com.example.employee.repository.EmployeeLoginRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final EmployeeLoginRepository loginRepo;

    public JwtUserDetailsService(EmployeeLoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeLogin login = loginRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String role = login.getRole() == null ? "USER" : login.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

        List<GrantedAuthority> authorities = List.of(authority);

        return User.builder()
                .username(login.getUsername())
                .password(login.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
