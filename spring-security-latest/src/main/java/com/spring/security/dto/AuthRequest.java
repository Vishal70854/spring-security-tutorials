package com.spring.security.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {
    // this class is a DTO prototype to get username and password of user so that
    // token can be generated for the user.
    private String userName;
    private String password;

}
