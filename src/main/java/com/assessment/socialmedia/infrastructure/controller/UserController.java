package com.assessment.socialmedia.infrastructure.controller;

import com.assessment.socialmedia.infrastructure.model.ApiResponseJSON;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.UserManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.UserCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/users", headers = {"Authorization"})
@RequiredArgsConstructor
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<PagedDataResponse<UserResponse>>> getUsers(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        PagedDataResponse<UserResponse> responseList = userManagementUseCase.getAllUsers(authenticatedUser, size, page);
        ApiResponseJSON<PagedDataResponse<UserResponse>> apiResponseJSON = new ApiResponseJSON<>("Request processed successfully.", responseList);
        return new ResponseEntity<>(apiResponseJSON, HttpStatus.OK);
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<UserResponse>> getUserByUsername(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                           @PathVariable("username") String username) {
        UserResponse response = userManagementUseCase.getUserByUsername(authenticatedUser, username);
        return new ResponseEntity<>(new ApiResponseJSON<>("Request processed successfully.", response), HttpStatus.OK);
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> updateUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @RequestParam(value = "email", required = false) String email,
                                                              @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        userManagementUseCase.updateUser(authenticatedUser, email, profilePicture);
        return new ResponseEntity<>(new ApiResponseJSON<>("User updated successfully."), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> deleteUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        userManagementUseCase.deleteUser(authenticatedUser);
        return new ResponseEntity<>(new ApiResponseJSON<>("User deleted successfully."), HttpStatus.OK);
    }

    @PutMapping(value = "/{username}/follow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> followUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @PathVariable("username") String username) {
        userManagementUseCase.followUser(authenticatedUser, username);
        return new ResponseEntity<>(new ApiResponseJSON<>("User followed successfully."), HttpStatus.OK);
    }

    @PutMapping(value = "/{username}/unfollow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> unfollowUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @PathVariable("username") String username) {
        userManagementUseCase.unfollowUser(authenticatedUser, username);
        return new ResponseEntity<>(new ApiResponseJSON<>("User unfollowed successfully."), HttpStatus.OK);
    }

}
