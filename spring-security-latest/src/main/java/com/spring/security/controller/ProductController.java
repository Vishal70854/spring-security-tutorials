package com.spring.security.controller;

import com.spring.security.dto.Product;
import com.spring.security.entity.UserInfo;
import com.spring.security.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

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
}
