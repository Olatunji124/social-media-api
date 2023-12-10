package com.assessment.socialmedia.usecases;

import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceFileUseCase {
    ResourceFileEntity createResourceFile(MultipartFile file);

    void updateProfilePicture(ResourceFileEntity resourceFile, MultipartFile profilePicture);

    ResourceFileEntity getFileById(Long id);
}
