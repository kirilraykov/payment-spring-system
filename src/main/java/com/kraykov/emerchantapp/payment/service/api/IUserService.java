package com.kraykov.emerchantapp.payment.service.api;

import com.kraykov.emerchantapp.payment.model.user.ImportUsersResponse;
import com.kraykov.emerchantapp.payment.model.user.User;
import com.kraykov.emerchantapp.payment.model.user.UserSignupResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService {
    UserSignupResponse registerUser(User user);
    ImportUsersResponse importUsers(MultipartFile file) throws IOException;
}
