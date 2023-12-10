package com.assessment.socialmedia.infrastructure.persistence.repository;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowerRepository extends JpaRepository<FollowerEntity, Long> {
    Optional<FollowerEntity> findByFollowerAndAppUserAndRecordStatus(AppUserEntity follower, AppUserEntity appUser, RecordStatusConstant active);
}
