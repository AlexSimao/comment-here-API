package com.alex.projectComment.infra.security;

import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsernameAndStatusIgnoreCase(username, StatusEnum.ACTIVE).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

    Set<GrantedAuthority> authorities = user.getRoles() == null ? Set.of()
        : user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // Converte Role para GrantedAuthority
        .collect(Collectors.toSet());

    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
        authorities);
  }
}