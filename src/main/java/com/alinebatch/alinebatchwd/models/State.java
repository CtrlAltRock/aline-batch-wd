package com.alinebatch.alinebatchwd.models;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Setter
@Getter
@Data
public class State {

    private String name;

    private String abbreviation;

    private String capital;

    private ArrayList<String> zips;

}
