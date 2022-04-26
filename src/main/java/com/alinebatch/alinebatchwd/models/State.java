package com.alinebatch.alinebatchwd.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Setter
@Getter
public class State {

    private String name;

    private String abbreviation;

    private String capital;

    private ArrayList<String> zips;

}
