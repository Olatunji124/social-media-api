package com.assessment.socialmedia.usecases;

import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.data.request.PostCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;

public interface PostManagementUseCase {
    void createPost(AuthenticatedUser authenticatedUser, PostCreationRequest request);
    PagedDataResponse<PostResponse> getAllPosts(AuthenticatedUser authenticatedUser, int size, int page);
    PostResponse getPostById(AuthenticatedUser authenticatedUser, Long postId);
    void editPost(AuthenticatedUser authenticatedUser, Long postId, String content);
    void deletePost(AuthenticatedUser authenticatedUser, Long postId);
    void likePost(AuthenticatedUser authenticatedUser, Long postId);
}
