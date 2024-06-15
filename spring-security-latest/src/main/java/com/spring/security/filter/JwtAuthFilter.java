package com.spring.security.filter;

import com.spring.security.config.UserInfoUserDetailsService;
import com.spring.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// this class is used to filter jwt token and decode it.After decoding it we will check the user details
// to authenticate the user so that Authentication manager will understand decoded jwt and will allow the user to access the http endpoints/services
// note: AuthenticationManager will not be able to understand jwt directly. we have to decode it and AuthenticationManager will be able to understand it to authenticate the user

// JwtAuthFilter is a filter class which intercepts each http request(via controller) and it will extract the jwt token, validate the token
// do all sort of validations whether the token is valid or not
// also in SecurityConfig class we have provided JwtAuthFilter as our default filter and after it only other filters will be executed
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // before request goes to any controller first it will come here and it will run the doFilterInternal() method to validate token

    @Autowired
    private JwtService jwtService;  // inject bean of JwtService
    
    @Autowired
    private UserInfoUserDetailsService userDetailsService;  // inject bean of UserDetailsService
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get the token from Authorization header which we passed in postman client
        String authHeader = request.getHeader("Authorization");
        String token = null;    // extract the token from authHeader
        String userName = null;
        //Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJWaXNoYWwiLCJpYXQiOjE3MTg0NTA2NTQsImV4cCI6MTcxODQ1MjQ1NH0.4Mal9Lhli_DVFA1sq-uDrqvgvJR-smYMSAi-2kEFhBQ

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);    // get the token from 7th index till last as till 7th character it is "Bearer "

            userName = jwtService.extractUserName(token);  // extract userName from the jwt token
        }
            // if userName != null and Authentication object == null then
            // load the userName in UserDetailsService to authenticate and create token, sent it to SecurityContextHolder
            // so that user can be authenticated
            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                if(jwtService.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                                                userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
            filterChain.doFilter(request, response);

    }
}
