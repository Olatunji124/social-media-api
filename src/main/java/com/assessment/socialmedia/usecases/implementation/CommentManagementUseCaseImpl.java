package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.PostEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.CommentEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.CommentEntityDao;
import com.assessment.socialmedia.usecases.CommentManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.CommentCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import com.assessment.socialmedia.usecases.exceptions.RequestForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentManagementUseCaseImpl implements CommentManagementUseCase {

    private PostEntityDao postEntityDao;
    private AppUserEntityDao appUserEntityDao;
    private CommentEntityDao commentEntityDao;

    @Override
    public void createComment(AuthenticatedUser authenticatedUser, CommentCreationRequest request) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        PostEntity post = postEntityDao.getRecordById(request.getPostId());
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .creator(appUser)
                .post(post)
                .build();
        commentEntityDao.saveRecord(comment);
    }

    @Override
    public PagedDataResponse<PostResponse> getAllComments(AuthenticatedUser authenticatedUser, Long postId, int size, int page) {
        PostEntity post = postEntityDao.getRecordById(postId);
        if (RecordStatusConstant.DELETED.equals(post.getRecordStatus())) {
            throw new NotFoundException("Post does not exist.");
        }
        Page<CommentEntity> entityPage = commentEntityDao.getAllComments(post, size, page);
        return new PagedDataResponse<>(entityPage.getTotalElements(), entityPage.getTotalPages(), entityPage.get()
                .map(this::fromEntityToResponse).collect(Collectors.toList()));
    }

    @Override
    public void editComment(AuthenticatedUser authenticatedUser, Long commentId, String content) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        CommentEntity comment = commentEntityDao.getRecordById(commentId);
        if (RecordStatusConstant.DELETED.equals(comment.getRecordStatus())) {
            throw new NotFoundException("Comment does not exist.");
        }
        if (!Objects.equals(appUser, comment.getCreator())) {
            throw new RequestForbiddenException("Request denied.");
        }
        comment.setContent(content);
        commentEntityDao.saveRecord(comment);
    }

    @Override
    public void deleteComment(AuthenticatedUser authenticatedUser, Long commentId) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        CommentEntity comment = commentEntityDao.getRecordById(commentId);
        if (RecordStatusConstant.DELETED.equals(comment.getRecordStatus())) {
            throw new NotFoundException("Comment does not exist.");
        }
        if (!Objects.equals(appUser, comment.getCreator())) {
            throw new RequestForbiddenException("Request denied.");
        }
        comment.setRecordStatus(RecordStatusConstant.DELETED);
        commentEntityDao.saveRecord(comment);
    }

    private PostResponse fromEntityToResponse(CommentEntity commentEntity) {
        return PostResponse.builder()
                .id(commentEntity.getId())
                .content(commentEntity.getContent())
                .creationDate(commentEntity.getDateCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
