package com.moviereview.controller;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.moviereview.user.controller.UserController;
import com.moviereview.user.model.User;
import com.moviereview.user.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
   private MockMvc mockMvc;
    @MockBean   
    private UserService userService;
    private UserController userController;
    

    @Test
    public void testGetAllUsers() throws Exception {
        
    }
}