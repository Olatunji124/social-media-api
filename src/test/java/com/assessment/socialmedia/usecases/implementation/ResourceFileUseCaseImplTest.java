package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.ResourceFileEntityDao;
import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import com.assessment.socialmedia.usecases.exceptions.BadRequestException;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceFileUseCaseImplTest {

    @Mock
    private ResourceFileEntityDao mockResourceFileEntityDao;

    private ResourceFileUseCaseImpl resourceFileUseCaseImplUnderTest;

    @BeforeEach
    void setUp() {
        resourceFileUseCaseImplUnderTest = new ResourceFileUseCaseImpl(mockResourceFileEntityDao);
    }

    @Test
    void testCreateResourceFile() {
        // Setup
        byte[] bytes = new byte[100];
        final MultipartFile file = new MockMultipartFile("name", bytes);
        final ResourceFileEntity expectedResult = ResourceFileEntity.builder()
                .contentType("contentType")
                .fileId("fileId")
                .fileName("fileName")
                .fileSizeInKb(10L)
                .resourceData("content".getBytes())
                .build();

        // Run the test
        final ResourceFileEntity result = resourceFileUseCaseImplUnderTest.createResourceFile(file);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockResourceFileEntityDao).saveRecord(ResourceFileEntity.builder()
                .contentType("contentType")
                .fileId("fileId")
                .fileName("fileName")
                .fileSizeInKb(0L)
                .resourceData(bytes)
                .build());
    }

    @Test
    void testCreateResourceFileThrowsException() {
        // Setup
        byte[] bytes = new byte[10];
        final MultipartFile file = new MockMultipartFile("name", bytes);
        final ResourceFileEntity expectedResult = ResourceFileEntity.builder()
                .contentType("contentType")
                .fileId("fileId")
                .fileName("fileName")
                .fileSizeInKb(10L)
                .resourceData("content".getBytes())
                .build();

        // Verify the results
        assertThatThrownBy(() -> resourceFileUseCaseImplUnderTest.createResourceFile(file)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void testUpdateProfilePicture() {
        // Setup
        final ResourceFileEntity resourceFile = ResourceFileEntity.builder()
                .contentType("contentType")
                .fileId("fileId")
                .fileName("fileName")
                .fileSizeInKb(0L)
                .resourceData(new byte[100])
                .build();
        final MultipartFile profilePicture = new MockMultipartFile("name", new byte[100]);

        // Run the test
        resourceFileUseCaseImplUnderTest.updateProfilePicture(resourceFile, profilePicture);
    }

    @Test
    void testUpdateProfilePictureThrowsException() {
        // Setup
        final ResourceFileEntity resourceFile = ResourceFileEntity.builder()
                .contentType("contentType")
                .fileId("fileId")
                .fileName("fileName")
                .fileSizeInKb(0L)
                .resourceData(new byte[100])
                .build();
        final MultipartFile profilePicture = new MockMultipartFile("name", new byte[10]);

        // Verify the results
        assertThatThrownBy(() -> resourceFileUseCaseImplUnderTest.updateProfilePicture(resourceFile, profilePicture)).isInstanceOf(BadRequestException.class);
    }
}
