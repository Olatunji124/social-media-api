package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.infrastructure.persistence.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEntityDaoImplTest {

    @Mock
    private PostRepository mockRepository;

    private PostEntityDaoImpl postEntityDaoImplUnderTest;

    @BeforeEach
    void setUp() {
        postEntityDaoImplUnderTest = new PostEntityDaoImpl(mockRepository);
    }

    @Test
    void testGetAllPosts() {
        // Setup
        // Configure PostRepository.findAll(...).
        final Page<PostEntity> postEntities = new PageImpl<>(List.of(PostEntity.builder()
                .appUser(new AppUserEntity())
                .content("content")
                .likes(1)
                .build()));
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(postEntities);

        // Run the test
        final Page<PostEntity> result = postEntityDaoImplUnderTest.getAllPosts(10, 0);

        // Verify the results
        assertThat(result).isEqualTo(postEntities);
    }

    @Test
    void testGetAllPosts_PostRepositoryReturnsNoItems() {
        // Setup
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<PostEntity> result = postEntityDaoImplUnderTest.getAllPosts(10, 0);

        // Verify the results
        assertThat(result).isEmpty();
    }
}
