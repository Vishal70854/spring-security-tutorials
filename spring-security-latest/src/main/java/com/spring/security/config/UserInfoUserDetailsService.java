package com.spring.security.config;

import com.spring.security.config.UserInfoUserDetails;
import com.spring.security.entity.UserInfo;
import com.spring.security.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component  // create a bean of this class at the start of spring boot project
public class UserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserInfoRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userInfo = repository.findByName(username);  // get UserInfo details from database

        // now we have to return UserDetails object as return type so for that we have created UserInfoUserDetails which implements UserDetails interface
        // implement all the methods and add the userInfo object there and finally return the UserInfoUserDetails object

        return userInfo.map(UserInfoUserDetails::new)  // map/convert UserInfo object to UserInfoUserDetails object
                .orElseThrow(() -> new UsernameNotFoundException("User not found " +username));

    }
}
