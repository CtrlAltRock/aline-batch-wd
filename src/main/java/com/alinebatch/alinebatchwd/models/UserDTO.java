package com.alinebatch.alinebatchwd.models;


import lombok.*;

import java.util.ArrayList;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {


    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    private String phone;

    private String password;

    private String email;

    private ArrayList<CardDTO> cards;

    private ArrayList<TransactionDTO> deposits = new ArrayList<>();

    private int ibCount;

    public void incrementIb()
    {
        this.ibCount += 1;
    }


}
