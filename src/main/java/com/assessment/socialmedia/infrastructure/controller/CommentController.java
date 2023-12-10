package com.assessment.socialmedia.infrastructure.controller;

import com.assessment.socialmedia.infrastructure.model.ApiResponseJSON;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.CommentManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.CommentCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/comment", headers = {"Authorization"})
@RequiredArgsConstructor
public class CommentController {

    private final CommentManagementUseCase commentManagementUseCase;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> createComment(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @RequestBody @Valid CommentCreationRequest request) {
        commentManagementUseCase.createComment(authenticatedUser, request);
        return new ResponseEntity<>(new ApiResponseJSON<>("Comment created successfully."), HttpStatus.CREATED);
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<PagedDataResponse<PostResponse>>> getComments(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                        @RequestParam(value = "postId") Long postId,
                                                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        PagedDataResponse<PostResponse> responseList = commentManagementUseCase.getAllComments(authenticatedUser, postId, size, page);
        ApiResponseJSON<PagedDataResponse<PostResponse>> apiResponseJSON = new ApiResponseJSON<>("Request processed successfully.", responseList);
        return new ResponseEntity<>(apiResponseJSON, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> editComment(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                            @PathVariable("id") Long commentId,
                                                            @RequestParam(value = "content") String content) {
        commentManagementUseCase.editComment(authenticatedUser, commentId, content);
        return new ResponseEntity<>(new ApiResponseJSON<>("Comment edited successfully."), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> deleteComment(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @PathVariable("id") Long commentId) {
        commentManagementUseCase.deleteComment(authenticatedUser, commentId);
        return new ResponseEntity<>(new ApiResponseJSON<>("Post deleted successfully."), HttpStatus.OK);
    }
}
