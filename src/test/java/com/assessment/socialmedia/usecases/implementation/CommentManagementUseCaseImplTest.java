package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.PostEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.CommentEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.CommentEntityDao;
import com.assessment.socialmedia.usecases.data.request.CommentCreationRequest;
import com.assessment.socialmedia.usecases.data.response.PagedDataResponse;
import com.assessment.socialmedia.usecases.data.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentManagementUseCaseImplTest {

    @Mock
    private PostEntityDao mockPostEntityDao;
    @Mock
    private AppUserEntityDao mockAppUserEntityDao;
    @Mock
    private CommentEntityDao mockCommentEntityDao;

    private CommentManagementUseCaseImpl commentManagementUseCaseImplUnderTest;

    @BeforeEach
    void setUp() {
        commentManagementUseCaseImplUnderTest = new CommentManagementUseCaseImpl(mockPostEntityDao,
                mockAppUserEntityDao, mockCommentEntityDao);
    }

    @Test
    void testCreateComment() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final CommentCreationRequest request = new CommentCreationRequest();
        request.setContent("content");
        request.setPostId(0L);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(PostEntity.builder().build());

        // Run the test
        commentManagementUseCaseImplUnderTest.createComment(authenticatedUser, request);

        // Verify the results
        verify(mockCommentEntityDao).saveRecord(CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build());
    }

    @Test
    void testCreateComment_PostEntityDaoThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final CommentCreationRequest request = new CommentCreationRequest();
        request.setContent("content");
        request.setPostId(0L);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(
                () -> commentManagementUseCaseImplUnderTest.createComment(authenticatedUser, request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testGetAllComments() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<PostResponse> expectedResult = new PagedDataResponse<>(0L, 0L,
                List.of(PostResponse.builder()
                        .id(0L)
                        .content("content")
                        .creationDate(LocalDateTime.of(2023, 12, 10, 11, 12).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build()));
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(PostEntity.builder().build());

        // Configure CommentEntityDao.getAllComments(...).
        CommentEntity comment = CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build();
        comment.setDateCreated(LocalDateTime.of(2023, 12, 10, 11, 12));
        final Page<CommentEntity> commentEntities = new PageImpl<>(List.of(comment));
        when(mockCommentEntityDao.getAllComments(PostEntity.builder().build(), 0, 0)).thenReturn(commentEntities);

        // Run the test
        final PagedDataResponse<PostResponse> result = commentManagementUseCaseImplUnderTest.getAllComments(
                authenticatedUser, 0L, 0, 0);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetAllComments_PostEntityDaoThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(
                () -> commentManagementUseCaseImplUnderTest.getAllComments(authenticatedUser, 0L, 0, 0))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testGetAllComments_CommentEntityDaoReturnsNoItems() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<PostResponse> expectedResult = new PagedDataResponse<>(0L, 1L,
                List.of());
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(PostEntity.builder().build());
        when(mockCommentEntityDao.getAllComments(PostEntity.builder().build(), 0, 0))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final PagedDataResponse<PostResponse> result = commentManagementUseCaseImplUnderTest.getAllComments(
                authenticatedUser, 0L, 0, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testEditComment() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());

        // Configure CommentEntityDao.getRecordById(...).
        final CommentEntity comment = CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build();
        when(mockCommentEntityDao.getRecordById(0L)).thenReturn(comment);

        // Run the test
        commentManagementUseCaseImplUnderTest.editComment(authenticatedUser, 0L, "content");

        // Verify the results
        verify(mockCommentEntityDao).saveRecord(CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build());
    }

    @Test
    void testEditComment_CommentEntityDaoGetRecordByIdThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockCommentEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(
                () -> commentManagementUseCaseImplUnderTest.editComment(authenticatedUser, 0L, "content"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testDeleteComment() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());

        // Configure CommentEntityDao.getRecordById(...).
        final CommentEntity comment = CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build();
        when(mockCommentEntityDao.getRecordById(0L)).thenReturn(comment);

        // Run the test
        commentManagementUseCaseImplUnderTest.deleteComment(authenticatedUser, 0L);

        // Verify the results
        verify(mockCommentEntityDao).saveRecord(CommentEntity.builder()
                .content("content")
                .creator(AppUserEntity.builder().build())
                .post(PostEntity.builder().build())
                .build());
    }

    @Test
    void testDeleteComment_CommentEntityDaoGetRecordByIdThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockCommentEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(
                () -> commentManagementUseCaseImplUnderTest.deleteComment(authenticatedUser, 0L))
                .isInstanceOf(RuntimeException.class);
    }
}
