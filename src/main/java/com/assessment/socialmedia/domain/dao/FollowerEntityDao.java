package com.assessment.socialmedia.domain.dao;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;

import java.util.Optional;

public interface FollowerEntityDao extends CrudDao<FollowerEntity, Long> {
    Optional<FollowerEntity> getByFollowerAndAppUser(AppUserEntity follower, AppUserEntity appUser);
}
