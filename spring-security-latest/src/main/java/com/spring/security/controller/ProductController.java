package com.spring.security.controller;

import com.spring.security.dto.AuthRequest;
import com.spring.security.dto.Product;
import com.spring.security.entity.UserInfo;
import com.spring.security.service.JwtService;
import com.spring.security.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtService jwtService;

    // inject dependency of AuthenticationManager which will check if we have the user with same username and password in db
    // then only allow the user to call JwtService to generate token
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome !!! This endpoint is not secured";
    }

    // add a new userInfo in database
    @PostMapping("/new")
    public String addNewUser(@RequestBody UserInfo userInfo){
        return productService.addUser(userInfo);
    }

    // we want  only ADMIN can acces getAllProducts() api, this is done by method level security i.e @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // method level security is enabled by using an annotation i.e @EnableMethodSecurity in config class
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // method level security configuration where we want only admin to access all products list
    public List<Product> getAllProducts(){
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')") // method level security configuration where we want only user role can access productById
    public Product getProductById(@PathVariable int id){

        return productService.getProduct(id);
    }

    // an endpoint to authenticate user and generate token so that after 1st login this token will be used by the server to authenticate user details
    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest){
        // authenticate if userName and password is correct in db with authenticationManager
        // and only allow if userName and password is correct

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));

        // check if authentication is successful then only generate jwt token for that user
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(authRequest.getUserName());
        }
        else{
            throw new UsernameNotFoundException("Invalid User Request");
        }

    }

}
