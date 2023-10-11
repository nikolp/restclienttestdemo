package com.example.restclienttestdemo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable {
    int id;
    String name;
}
