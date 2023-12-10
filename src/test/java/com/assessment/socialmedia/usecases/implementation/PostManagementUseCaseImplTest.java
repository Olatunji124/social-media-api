package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.PostEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.usecases.data.request.PostCreationRequest;
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
class PostManagementUseCaseImplTest {

    @Mock
    private AppUserEntityDao mockAppUserEntityDao;
    @Mock
    private PostEntityDao mockPostEntityDao;

    private PostManagementUseCaseImpl postManagementUseCaseImplUnderTest;

    @BeforeEach
    void setUp() {
        postManagementUseCaseImplUnderTest = new PostManagementUseCaseImpl(mockAppUserEntityDao, mockPostEntityDao);
    }

    @Test
    void testCreatePost() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PostCreationRequest request = new PostCreationRequest();
        request.setContent("content");

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());

        // Run the test
        postManagementUseCaseImplUnderTest.createPost(authenticatedUser, request);

        // Verify the results
        verify(mockPostEntityDao).saveRecord(PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build());
    }

    @Test
    void testGetAllPosts() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<PostResponse> expectedResult = new PagedDataResponse<>(1L, 1L,
                List.of(PostResponse.builder()
                        .id(0L)
                        .content("content")
                        .likes(0)
                        .creationDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build()));

        // Configure PostEntityDao.getAllPosts(...).
        PostEntity post = PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build();
        post.setDateCreated(LocalDateTime.now());
        final Page<PostEntity> postEntities = new PageImpl<>(List.of(post));
        when(mockPostEntityDao.getAllPosts(10, 0)).thenReturn(postEntities);

        // Run the test
        final PagedDataResponse<PostResponse> result = postManagementUseCaseImplUnderTest.getAllPosts(authenticatedUser,
                10, 0);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetAllPosts_PostEntityDaoReturnsNoItems() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<PostResponse> expectedResult = new PagedDataResponse<>(0L, 1L,
                List.of());
        when(mockPostEntityDao.getAllPosts(0, 0)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final PagedDataResponse<PostResponse> result = postManagementUseCaseImplUnderTest.getAllPosts(authenticatedUser,
                0, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetPostById() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PostResponse expectedResult = PostResponse.builder()
                .id(0L)
                .content("content")
                .likes(0)
                .creationDate(LocalDateTime.of(2023, 12, 10, 11, 12).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        // Configure PostEntityDao.getRecordById(...).
        final PostEntity post = PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build();
        post.setDateCreated(LocalDateTime.of(2023, 12, 10, 11, 12));
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(post);

        // Run the test
        final PostResponse result = postManagementUseCaseImplUnderTest.getPostById(authenticatedUser, 0L);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetPostById_PostEntityDaoThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(() -> postManagementUseCaseImplUnderTest.getPostById(authenticatedUser, 0L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testEditPost() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());

        // Configure PostEntityDao.getRecordById(...).
        final PostEntity post = PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build();
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(post);

        // Run the test
        postManagementUseCaseImplUnderTest.editPost(authenticatedUser, 0L, "content");

        // Verify the results
        verify(mockPostEntityDao).saveRecord(PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build());
    }

    @Test
    void testEditPost_PostEntityDaoGetRecordByIdThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(
                () -> postManagementUseCaseImplUnderTest.editPost(authenticatedUser, 0L, "content"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testDeletePost() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());

        // Configure PostEntityDao.getRecordById(...).
        final PostEntity post = PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build();
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(post);

        // Run the test
        postManagementUseCaseImplUnderTest.deletePost(authenticatedUser, 0L);

        // Verify the results
        verify(mockPostEntityDao).saveRecord(PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build());
    }

    @Test
    void testDeletePost_PostEntityDaoGetRecordByIdThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(AppUserEntity.builder().build());
        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(() -> postManagementUseCaseImplUnderTest.deletePost(authenticatedUser, 0L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testLikePost() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        // Configure PostEntityDao.getRecordById(...).
        final PostEntity post = PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build();
        when(mockPostEntityDao.getRecordById(0L)).thenReturn(post);

        // Run the test
        postManagementUseCaseImplUnderTest.likePost(authenticatedUser, 0L);

        // Verify the results
        verify(mockPostEntityDao).saveRecord(PostEntity.builder()
                .content("content")
                .appUser(AppUserEntity.builder().build())
                .likes(0)
                .build());
    }

    @Test
    void testLikePost_PostEntityDaoGetRecordByIdThrowsRuntimeException() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        when(mockPostEntityDao.getRecordById(0L)).thenThrow(RuntimeException.class);

        // Run the test
        assertThatThrownBy(() -> postManagementUseCaseImplUnderTest.likePost(authenticatedUser, 0L))
                .isInstanceOf(RuntimeException.class);
    }
}
