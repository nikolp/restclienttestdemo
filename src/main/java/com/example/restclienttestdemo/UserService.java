package com.example.restclienttestdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserService(RestTemplateBuilder restTemplateBuilder,
                       ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.rootUri("https://reqres.in").build();
        this.objectMapper = objectMapper;
    }

    public User getSingleUser(Long id) {
        ResponseEntity<User> responseEntity = this.restTemplate
                .exchange("/api/users/{id}", HttpMethod.GET, makeHttpEntityForGet(), User.class, id);
        return responseEntity.getBody();
    }

    // Save a brand new user to database
    public User postUser(User user) {
        ResponseEntity<User> responseEntity = this.restTemplate
                .exchange("/api/users", HttpMethod.POST, makeHttpEntityForPost(user), User.class);
        return responseEntity.getBody();
    }

    HttpEntity<Void> makeHttpEntityForGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    HttpEntity<String> makeHttpEntityForPost(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        String postContentsAsString = "";
        try {
            postContentsAsString = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return new HttpEntity<>(postContentsAsString, headers);
    }

}
