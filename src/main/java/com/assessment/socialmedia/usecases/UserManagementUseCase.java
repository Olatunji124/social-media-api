package com.assessment.socialmedia.usecases;

import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.data.request.LoginRequest;
import com.assessment.socialmedia.usecases.data.request.UserCreationRequest;
import com.assessment.socialmedia.usecases.data.response.LoginResponse;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserManagementUseCase {
    void createUser(UserCreationRequest request);
    LoginResponse login(LoginRequest request);
    PagedDataResponse<UserResponse> getAllUsers(AuthenticatedUser authenticatedUser, int size, int page);
    UserResponse getUserByUsername(AuthenticatedUser authenticatedUser, String username);

    void updateUser(AuthenticatedUser authenticatedUser, String email, MultipartFile profilePicture);
    void deleteUser(AuthenticatedUser authenticatedUser);

    void followUser(AuthenticatedUser authenticatedUser, String username);

    void unfollowUser(AuthenticatedUser authenticatedUser, String username);
}
