package com.assessment.socialmedia.domain.dao;


import com.assessment.socialmedia.domain.entities.AppUserEntity;
import org.springframework.data.domain.Page;

import java.util.Optional;


public interface AppUserEntityDao extends CrudDao<AppUserEntity, Long> {
    Optional<AppUserEntity> findByUsername(String username);
    AppUserEntity getByUsername(String username);
    boolean existsByEmail(String email);

    Page<AppUserEntity> getAllUsers(int size, int page);
}
