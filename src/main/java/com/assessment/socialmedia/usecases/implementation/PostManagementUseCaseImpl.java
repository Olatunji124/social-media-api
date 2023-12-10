package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.PostEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.PostManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.PostCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import com.assessment.socialmedia.usecases.exceptions.RequestForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostManagementUseCaseImpl implements PostManagementUseCase {

    private final AppUserEntityDao appUserEntityDao;
    private final PostEntityDao postEntityDao;

    @Override
    public void createPost(AuthenticatedUser authenticatedUser, PostCreationRequest request) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        PostEntity post = PostEntity.builder()
                .content(request.getContent())
                .appUser(appUser)
                .build();
        postEntityDao.saveRecord(post);
    }

    @Override
    public PagedDataResponse<PostResponse> getAllPosts(AuthenticatedUser authenticatedUser, int size, int page) {
        Page<PostEntity> entityPage = postEntityDao.getAllPosts(size, page);
        return new PagedDataResponse<>(entityPage.getTotalElements(), entityPage.getTotalPages(), entityPage.get()
                .map(this::fromEntityToResponse).collect(Collectors.toList()));
    }

    @Override
    public PostResponse getPostById(AuthenticatedUser authenticatedUser, Long postId) {
        PostEntity post = postEntityDao.getRecordById(postId);
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        return fromEntityToResponse(post);
    }

    @Override
    public void editPost(AuthenticatedUser authenticatedUser, Long postId, String content) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        PostEntity post = postEntityDao.getRecordById(postId);
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        if (!Objects.equals(appUser, post.getAppUser())) {
            throw new RequestForbiddenException("Request denied.");
        }
        post.setContent(content);
        postEntityDao.saveRecord(post);
    }

    @Override
    public void deletePost(AuthenticatedUser authenticatedUser, Long postId) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        PostEntity post = postEntityDao.getRecordById(postId);
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        if (!Objects.equals(appUser, post.getAppUser())) {
            throw new RequestForbiddenException("Request denied.");
        }
        post.setRecordStatus(RecordStatusConstant.DELETED);
        postEntityDao.saveRecord(post);
    }

    @Override
    public void likePost(AuthenticatedUser authenticatedUser, Long postId) {
        PostEntity post = postEntityDao.getRecordById(postId);
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        post.setLikes(post.getLikes() + 1);
        postEntityDao.saveRecord(post);
    }

    private PostResponse fromEntityToResponse(PostEntity postEntity) {
        return PostResponse.builder()
                .id(postEntity.getId())
                .content(postEntity.getContent())
                .creationDate(postEntity.getDateCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .likes(postEntity.getLikes())
                .build();
    }
}
