package com.assessment.socialmedia.infrastructure.persistence.repository;


import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceFileRepository extends JpaRepository<ResourceFileEntity, Long> {
}
