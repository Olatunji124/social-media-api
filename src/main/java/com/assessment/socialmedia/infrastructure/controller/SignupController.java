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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "api/v1/user")
@RequiredArgsConstructor
public class SignupController {

    private final UserManagementUseCase userManagementUseCase;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> createCustomer(@ModelAttribute @Valid UserCreationRequest request,
                                                                  @RequestParam(value = "profilePicture") MultipartFile profilePicture) {
        request.setProfilePicture(profilePicture);
        userManagementUseCase.createUser(request);
        return new ResponseEntity<>(new ApiResponseJSON<>("User created successfully."), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userManagementUseCase.login(request);
        return new ResponseEntity<>(new ApiResponseJSON<>("User created successfully.", response), HttpStatus.OK);
    }
}
