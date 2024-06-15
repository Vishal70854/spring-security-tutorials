package com.spring.security.repository;

import com.spring.security.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    // created a method to find UserInfo object by name i.e findByName
    // convention goes like to get some output with fields of Table, we can use
    // findByColumnName with each column name should start with Capital letters
    Optional<UserInfo> findByName(String username); // find UserInfo object by name

}
