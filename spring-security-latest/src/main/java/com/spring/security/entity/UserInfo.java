package com.spring.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto generate the value of primary key for each row
    private int id;
    private String name;
    private String email;
    private String password;
    private String roles;
}
