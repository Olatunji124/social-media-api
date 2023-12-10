package com.assessment.socialmedia.infrastructure.controller;

import com.assessment.socialmedia.infrastructure.model.ApiResponseJSON;
import com.assessment.socialmedia.usecases.UserManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.LoginRequest;
import com.assessment.socialmedia.usecases.data.request.UserCreationRequest;
import com.assessment.socialmedia.usecases.data.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/user")
@RequiredArgsConstructor
public class SignupController {

    private final UserManagementUseCase userManagementUseCase;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> createCustomer(@RequestBody @Valid UserCreationRequest request) {
        userManagementUseCase.createUser(request);
        return new ResponseEntity<>(new ApiResponseJSON<>("User created successfully."), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userManagementUseCase.login(request);
        return new ResponseEntity<>(new ApiResponseJSON<>("User created successfully.", response), HttpStatus.OK);
    }
}
