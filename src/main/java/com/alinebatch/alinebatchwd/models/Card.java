package com.alinebatch.alinebatchwd.models;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Card {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String expiration;

    @Column(nullable = false)
    private String cvv;

}
