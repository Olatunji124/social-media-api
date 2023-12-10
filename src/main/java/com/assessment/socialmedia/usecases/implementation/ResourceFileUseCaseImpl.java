package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.ResourceFileEntityDao;
import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import com.assessment.socialmedia.usecases.ResourceFileUseCase;
import com.assessment.socialmedia.usecases.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResourceFileUseCaseImpl implements ResourceFileUseCase {

    private final ResourceFileEntityDao resourceFileEntityDao;

    @SneakyThrows
    @Override
    public ResourceFileEntity createResourceFile(MultipartFile file) {
        if (file == null || file.getBytes().length < 100) {
            throw new BadRequestException("Please upload a valid image.");
        }
        long sizeInMb = Double.valueOf(file.getSize() * 1.0 / (1024 * 1024)).longValue();

        String imageName = file.getOriginalFilename();
        byte[] imageByteArray = file.getBytes();
        String fileId = RandomStringUtils.randomAlphanumeric(12);
        ResourceFileEntity fileEntity = ResourceFileEntity.builder()
                .fileId(fileId)
                .fileName(imageName)
                .contentType(file.getContentType())
                .fileSizeInKb(sizeInMb)
                .resourceData(imageByteArray)
                .build();
        resourceFileEntityDao.saveRecord(fileEntity);
        return fileEntity;
    }

    @SneakyThrows
    @Override
    public void updateProfilePicture(ResourceFileEntity resourceFile, MultipartFile profilePicture) {
        if (profilePicture.getBytes().length < 100) {
            throw new BadRequestException("Please upload a valid image.");
        }
        long sizeInMb = Double.valueOf(profilePicture.getSize() * 1.0 / (1024 * 1024)).longValue();
        resourceFile.setResourceData(profilePicture.getBytes());
        resourceFile.setFileName(profilePicture.getOriginalFilename());
        resourceFile.setContentType(profilePicture.getContentType());
        resourceFile.setFileSizeInKb(sizeInMb);
    }
}
