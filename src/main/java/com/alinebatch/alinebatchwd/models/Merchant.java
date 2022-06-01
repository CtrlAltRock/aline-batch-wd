package com.alinebatch.alinebatchwd.models;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class Merchant {

    private Long id;

    private String name;

    private String city;

    private String state;

    private String zip;

    private Boolean hadIb = false;

    private Boolean hadErrors = false;

    private Boolean hadOnline = false;

}
