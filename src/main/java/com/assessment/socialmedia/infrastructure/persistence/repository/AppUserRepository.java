package com.assessment.socialmedia.infrastructure.persistence.repository;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long>, JpaSpecificationExecutor<AppUserEntity> {
    boolean existsByEmailAndRecordStatus(String email, RecordStatusConstant active);

    Optional<AppUserEntity> findFirstByUsernameAndRecordStatus(String username, RecordStatusConstant status);
}
