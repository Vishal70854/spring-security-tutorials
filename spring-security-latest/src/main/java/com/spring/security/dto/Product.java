package com.spring.security.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private int productId;
    private String name;
    private int qty;
    private int price;
}
