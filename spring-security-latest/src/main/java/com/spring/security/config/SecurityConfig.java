package com.spring.security.config;

import com.spring.security.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // using this annotation we can define multiple beans for each method inside this class
@EnableWebSecurity // the @EnableWebSecurity annotation is a powerful tool that enables developers to configure Spring Security for a web application.
@EnableMethodSecurity(prePostEnabled = true)    // this annotation is to enable method level security where we can define which all user roles can access which endpoints(can be done with @PreAuthorize(),@PostAuthorize) etc
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // authentication
    @Bean   // create a bean of UserDetailsService to give details about the user which can use our application by authenticating
    // this is authentication of users
    public UserDetailsService userDetailsService(){
        /*
            //-- since we have hardcoded all the user details here itself. this is not what we want. we want to fetch user details from database
            // so we will fetch the user details from db by creating a class which will extends UserDetailsService and return the user details

        // defining a admin user details
        UserDetails admin = User.withUsername("Vishal")
                .password(encoder.encode("abc1"))
                .roles("ADMIN")
                .build();
        // defining another user details
        UserDetails user = User.withUsername("John")
                .password(encoder.encode("abc2"))
                .roles("USER","ADMIN","HR") // we can define multiple roles to an user
                .build();

        // save the user details in memory usingInMemoryUserDetailsManager(admin, user) for admin and user (users)
        // since we are not using db to store or fetch the details of user. so storing for now in memory to authenticate users

        return new InMemoryUserDetailsManager(admin, user);
        */

        return new UserInfoUserDetailsService();    // this class has implemented UserDetailsService interface which will load user by username and also
                                                    // converted UserInfoUserDetailsService to UserInfoUserDetails object as it expects UserInfoUserDetailsUserInfoUserDetails as return type
    }

    // authorization
    @Bean   // this bean if for SecurityFilterChain which is for authorization of users for
    // what endpoints we need to bypass and what endpoints need to be authenticated
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // disable csrf
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/products/welcome", "/products/new", "/products/authenticate").permitAll()   // permit all http requests with /products/welcome, /products/new, /products/authenticate
                        .anyRequest().authenticated()   // authenticate all other requests apart from /products/welcome, /products/new, /products/authenticate
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);    // use JwtAuthFilter first and then use default spring boot filter
        return http.build();

    }


    // create a bean of PasswordEncoder to encrypt password otherwise it may lead to security issues
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // create a bean of AuthenticationProvider which will talk to UserDetailsService and get the details required
    // if we don't create this bean then we will not be able to get the access the endpoints
    @Bean
    public AuthenticationProvider authenticationProvider(){
        // AuthenticationProvider will talk to UserDetailsService and generate the UserDetails object and sent it to Authentication object
        // create object of DaoAuthenticationProvider and set userDetailsService() and PasswordEncoder() in DaoAuthenticationProvider
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService()); // set the userDetailsService in authentication provider
        authenticationProvider.setPasswordEncoder(passwordEncoder());   // set the password encoder in authentication provider

        return authenticationProvider;  // now we will be able to access the endpoints via authentication provider.
    }

    // define a bean of AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
