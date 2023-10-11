package com.example.restclienttestdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

// https://rieckpil.de/testing-your-spring-resttemplate-with-restclienttest/
@RestClientTest(UserService.class)
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    void setUp() {
        this.mockRestServiceServer.reset();
    }

    @AfterEach
    void tearDown() {
        this.mockRestServiceServer.verify();
    }

    @Test
    void getSingleUser() {
        String json = """
        {
        "id": 1,
        "name": "Philip"
        }
     """;
        this.mockRestServiceServer
                .expect(requestTo("/api/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        User result = userService.getSingleUser(1L);
        assertEquals(1, result.id);
    }

    @SneakyThrows
    @Test
    void postUser() {
        User inputUser = new User();
        inputUser.setId(0);
        inputUser.setName("Philip");

        User outputUser = objectMapper.readValue(objectMapper.writeValueAsString(inputUser), User.class);
        // Upon calling our service an id is generated
        outputUser.setId(1);
        String jsonResponse = objectMapper.writeValueAsString(outputUser);

        this.mockRestServiceServer
                .expect(requestTo("/api/users"))
                .andExpect(method(HttpMethod.POST))
                // Grab individual pieces from request body and see if they are right
                .andExpect(jsonPath("$.name").value("Philip"))
                .andExpect(jsonPath("$.id").value(0))
                // Check the entire post body, ignores whitespace and field order when comparing
                .andExpect(content().json("""
                {
                    "id":0,
                    "name":"Philip"
                 }"""
                        ))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));


        User result = userService.postUser(inputUser);
        assertEquals(1, result.id);
        assertEquals("Philip", result.getName());
    }
}