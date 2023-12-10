package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.entities.CommentEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.infrastructure.persistence.repository.CommentRepository;
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
class CommentEntityDaoImplTest {

    @Mock
    private CommentRepository mockRepository;

    private CommentEntityDaoImpl commentEntityDaoImplUnderTest;

    @BeforeEach
    void setUp() {
        commentEntityDaoImplUnderTest = new CommentEntityDaoImpl(mockRepository);
    }

    @Test
    void testGetAllComments() {
        // Setup
        final PostEntity post = PostEntity.builder().build();

        // Configure CommentRepository.findAll(...).
        final Page<CommentEntity> commentEntities = new PageImpl<>(List.of(CommentEntity.builder()
                .post(post)
                .content("content").build()));
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(commentEntities);

        // Run the test
        final Page<CommentEntity> result = commentEntityDaoImplUnderTest.getAllComments(post, 10, 0);

        // Verify the results
        assertThat(result).isEqualTo(commentEntities);
    }

    @Test
    void testGetAllComments_CommentRepositoryReturnsNoItems() {
        // Setup
        final PostEntity post = PostEntity.builder().build();
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<CommentEntity> result = commentEntityDaoImplUnderTest.getAllComments(post, 10, 0);

        // Verify the results
        assertThat(result).isEmpty();
    }
}
