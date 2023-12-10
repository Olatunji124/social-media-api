package com.assessment.socialmedia.infrastructure.controller;

import com.assessment.socialmedia.infrastructure.model.ApiResponseJSON;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.PostManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.PostCreationRequest;
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
@RequestMapping(value = "api/v1/post", headers = {"Authorization"})
@RequiredArgsConstructor
public class PostController {

    private final PostManagementUseCase postManagementUseCase;


    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> createPost(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @RequestBody @Valid PostCreationRequest request) {
        postManagementUseCase.createPost(authenticatedUser, request);
        return new ResponseEntity<>(new ApiResponseJSON<>("Post created successfully."), HttpStatus.CREATED);
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<PagedDataResponse<PostResponse>>> getPosts(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        PagedDataResponse<PostResponse> responseList = postManagementUseCase.getAllPosts(authenticatedUser, size, page);
        ApiResponseJSON<PagedDataResponse<PostResponse>> apiResponseJSON = new ApiResponseJSON<>("Request processed successfully.", responseList);
        return new ResponseEntity<>(apiResponseJSON, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<PostResponse>> getPostById(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                     @PathVariable("id") Long postId) {
        PostResponse response = postManagementUseCase.getPostById(authenticatedUser, postId);
        return new ResponseEntity<>(new ApiResponseJSON<>("Request processed successfully.", response), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> editPost(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                            @PathVariable("id") Long postId,
                                                            @RequestParam(value = "content") String content) {
        postManagementUseCase.editPost(authenticatedUser, postId, content);
        return new ResponseEntity<>(new ApiResponseJSON<>("Post edited successfully."), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> deleteUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                              @PathVariable("id") Long postId) {
        postManagementUseCase.deletePost(authenticatedUser, postId);
        return new ResponseEntity<>(new ApiResponseJSON<>("Post deleted successfully."), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/like", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseJSON<String>> likePost(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                            @PathVariable("id") Long postId) {
        postManagementUseCase.likePost(authenticatedUser, postId);
        return new ResponseEntity<>(new ApiResponseJSON<>("Post liked."), HttpStatus.OK);
    }
}
