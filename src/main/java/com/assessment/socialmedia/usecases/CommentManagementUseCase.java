package com.assessment.socialmedia.usecases;

import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.data.request.CommentCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;

public interface CommentManagementUseCase {
    void createComment(AuthenticatedUser authenticatedUser, CommentCreationRequest request);
    PagedDataResponse<PostResponse> getAllComments(AuthenticatedUser authenticatedUser, Long postId, int size, int page);
    void editComment(AuthenticatedUser authenticatedUser, Long commentId, String content);
    void deleteComment(AuthenticatedUser authenticatedUser, Long commentId);
}
