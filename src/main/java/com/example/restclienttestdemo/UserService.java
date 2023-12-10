package com.example.restclienttestdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserService(RestTemplateBuilder restTemplateBuilder,
                       ObjectMapper objectMapper) {
        // this.restTemplate = restTemplateBuilder.rootUri("https://reqres.in").build();
        this.restTemplate = restTemplateBuilder.rootUri("").build();
        this.objectMapper = objectMapper;
    }

    public User getSingleUser(Long id) {
        ResponseEntity<User> responseEntity = this.restTemplate
                .exchange("/api/users/{id}", HttpMethod.GET, makeHttpEntityForGet(), User.class, id);
        return responseEntity.getBody();
    }

    public User getSingleUserWithQueryParams(Long id, String name) {
        String baseUrl = "/api/users";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("id", id.toString())
                .queryParam("name", name);
        String urlWithParams = uriBuilder.toUriString();
        ResponseEntity<User> responseEntity = this.restTemplate
                .exchange(urlWithParams, HttpMethod.GET, makeHttpEntityForGet(), User.class);
        return responseEntity.getBody();
    }

    public User getSingleUserWithQueryParamsAndCustomEncoding(Long id, String name) {
        String encodedName;
        try {
            // Add .replace("+", "%20") at the end if you don't like spaces represented as +
            encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder("/api/users")
                .append("?id=")
                .append(id.toString())
                .append("&name=")
                .append(encodedName);
        String url = urlBuilder.toString();

        ResponseEntity<User> responseEntity = this.restTemplate
                .exchange(url, HttpMethod.GET, makeHttpEntityForGet(), User.class);
        return responseEntity.getBody();
    }

    // Save a brand-new user to database
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
