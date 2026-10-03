package com.example.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
            User user =userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User NOT Found: "+username));
            return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername()).password(user.getPassword()).roles("User").build();

    }
    
}
