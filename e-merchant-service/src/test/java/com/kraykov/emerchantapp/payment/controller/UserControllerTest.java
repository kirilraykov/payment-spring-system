package com.kraykov.emerchantapp.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraykov.emerchantapp.payment.auth.JwtUtils;
import com.kraykov.emerchantapp.payment.model.user.*;
import com.kraykov.emerchantapp.payment.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        Merchant newUser = new Merchant();
        UserSignupResponse expectedResponse = UserSignupResponse.builder()
                .message("Some test message")
                .username("username-test")
                .userEmail("username@email.com")
                .build();
        when(userService.registerUser(any(User.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.username").value(expectedResponse.getUsername()))
                .andExpect(jsonPath("$.userEmail").value(expectedResponse.getUserEmail()));
    }

    @Test
    public void testLoginUser() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .password("password123")
                .username("username-test")
                .build();
        String mockJwtToken = "mockJwtToken";

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("mockUsername");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        when(userService.getUserByUsername(loginRequest.getUsername())).thenReturn(new Merchant());
        when(jwtUtils.generateJwtToken(any(), any(User.class))).thenReturn(mockJwtToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockJwtToken));
    }

    @Test
    public void testImportUsers() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("csv-file", "filename.csv",
                "text/csv", "csvData".getBytes());
        ImportUsersResponse expectedResponse = ImportUsersResponse.builder()
                .totalUsersImported(10)
                .importedUsersMessage("Users have been correctly imported")
                .build();
        when(userService.importUsers(any(MultipartFile.class))).thenReturn(expectedResponse);

        mockMvc.perform(multipart("/api/users/import").file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsersImported").value(expectedResponse.getTotalUsersImported()))
                .andExpect(jsonPath("$.importedUsersMessage").value(expectedResponse.getImportedUsersMessage()));
    }
}
