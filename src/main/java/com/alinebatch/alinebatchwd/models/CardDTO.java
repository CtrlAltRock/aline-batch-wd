package com.alinebatch.alinebatchwd.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;

    private Long userId;

    private String cardNumber;

    private String expiration;

    private String cvv;
}
