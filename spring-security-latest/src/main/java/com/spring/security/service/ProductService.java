package com.spring.security.service;

import com.spring.security.dto.Product;
import com.spring.security.entity.UserInfo;
import com.spring.security.repository.UserInfoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {

    @Autowired
    private UserInfoRepository repository; // inject object dependency of UserInfoRepository to store userInfo object in database.

    @Autowired
    private PasswordEncoder passwordEncoder;    // to encrypt password and then store it in database

    private List<Product> productList = null;

//    Spring calls the methods annotated with @PostConstruct only once, just after the initialization of bean properties.
    @PostConstruct
    public void loadProductsFromDB(){
        productList = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> Product.builder()
                        .productId(i)
                        .name("product" + i)
                        .qty(new Random().nextInt(10))
                        .price(new Random().nextInt(5000)).build()
                ).collect(Collectors.toList());
    }

    // get all products
    public List<Product> getProducts(){
        return productList;
    }

    // get product by id
    public Product getProduct(int id){
        return productList.stream()
                .filter(product -> product.getProductId() == id)// filter each product with productId == id
                .findAny()  // return any Product object with ProductId == id
                .orElseThrow(() -> new RuntimeException("product with " + id + " is not found"));
    }

    // add a user in database
    public String addUser(UserInfo userInfo){
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword())); // get the password and encode it and then store the userInfo object in db
        repository.save(userInfo);  // save the userInfo object in database

        return "User added to system";
    }

}
