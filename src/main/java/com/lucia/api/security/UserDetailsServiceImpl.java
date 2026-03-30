package com.lucia.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lucia.api.model.entity.User;
import com.lucia.api.repository.User.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Accept either email or numeric ID (Long) as username
            User profileEntity;

            if (username.contains("@")) {
                Optional<User> byEmail = userRepository.findByEmail(username);
                profileEntity = byEmail.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado para email: " + username));
            } else {
                Long userId = Long.parseLong(username);
                profileEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(profileEntity.getId().toString())
                    .password("") // No usamos password con JWT
                    .authorities(getAuthorities(profileEntity))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Formato de usuario inválido: " + username);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User profile) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (profile.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority(profile.getRole().apiAuthority()));
        } else {
            authorities.add(new SimpleGrantedAuthority("client"));
        }

        return authorities;
    }
}
