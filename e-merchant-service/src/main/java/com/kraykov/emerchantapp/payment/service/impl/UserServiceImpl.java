package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.exception.ErrorCodes;
import com.kraykov.emerchantapp.payment.model.MerchantStatus;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.*;
import com.kraykov.emerchantapp.payment.repository.UserRepository;
import com.kraykov.emerchantapp.payment.service.api.IUserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.kraykov.emerchantapp.payment.exception.ErrorCodes.MISSING_USER_FOR_USERNAME;
import static java.lang.String.format;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserSignupResponse registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return UserSignupResponse.builder()
                .username(user.getUsername())
                .userEmail(user.getEmail())
                .message(format("New user with username: %s was created. Please login to continue.", user.getUsername()))
                .build();
    }

    @Override
    public ImportUsersResponse importUsers(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
        int totalUsersImported = 0;

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                User user = createUserFromCsvRecord(csvRecord);
                totalUsersImported++;
                users.add(user);
            }
        }

        // Call the signup method for each user
        users.forEach(this::registerUser);

        return ImportUsersResponse.builder()
                .totalUsersImported(totalUsersImported)
                .importedUsersMessage(totalUsersImported + " Users have been successfully imported.")
                .build();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new CustomServiceException(MISSING_USER_FOR_USERNAME.getCode(),
                        "No user exists for username: " + username)
        );
    }

    private User createUserFromCsvRecord(CSVRecord csvRecord) {
        String userType = csvRecord.get("userType");

        return switch (userType.toLowerCase()) {
            case "admin" -> Admin.builder()
                    .adminLevel(Integer.parseInt(csvRecord.get("adminLevel")))
                    .adminLocation(csvRecord.get("adminLocation"))
                    .name(csvRecord.get("name"))
                    .username(csvRecord.get("username"))
                    .email(csvRecord.get("email"))
                    .password(csvRecord.get("password"))
                    .userType(userType)
                    .build();
            case "merchant" -> Merchant.builder()
                    .description(csvRecord.get("description"))
                    .status(MerchantStatus.valueOf(csvRecord.get("status").toUpperCase()))
                    .totalTransactionSum(BigDecimal.ZERO)
                    .name(csvRecord.get("name"))
                    .username(csvRecord.get("username"))
                    .email(csvRecord.get("email"))
                    .password(csvRecord.get("password"))
                    .userType(userType)
                    .build();
            default -> throw new CustomServiceException(ErrorCodes.MISSING_USER_TYPE.getCode(),
                    "Missing user type. Please provide user type when importing/registering new user.");
        };
    }


}
