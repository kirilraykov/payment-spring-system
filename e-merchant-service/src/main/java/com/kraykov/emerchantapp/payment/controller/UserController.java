package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.auth.JwtUtils;
import com.kraykov.emerchantapp.payment.model.user.ImportUsersResponse;
import com.kraykov.emerchantapp.payment.model.user.LoginRequest;
import com.kraykov.emerchantapp.payment.model.user.User;
import com.kraykov.emerchantapp.payment.model.user.UserSignupResponse;
import com.kraykov.emerchantapp.payment.service.api.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final IUserService userService;
    private final JwtUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(IUserService userService, JwtUtils jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/auth/signup")
    public UserSignupResponse registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public String createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User authenticatedUser = userService.getUserByUsername(loginRequest.getUsername());
        return jwtTokenUtil.generateJwtToken(authentication, authenticatedUser);
    }

    @PostMapping(value = "/users/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportUsersResponse importUsers(@RequestPart("csv-file") MultipartFile file) throws IOException {
        return userService.importUsers(file);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }
    }
}
