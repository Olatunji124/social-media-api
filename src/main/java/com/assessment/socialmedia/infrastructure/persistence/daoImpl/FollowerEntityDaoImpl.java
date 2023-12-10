package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.dao.FollowerEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.FollowerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowerEntityDaoImpl extends CrudDaoImpl<FollowerEntity, Long> implements FollowerEntityDao {

    private final FollowerRepository repository;
    public FollowerEntityDaoImpl(FollowerRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<FollowerEntity> getByFollowerAndAppUser(AppUserEntity follower, AppUserEntity appUser) {
        return repository.findByFollowerAndAppUserAndRecordStatus(follower, appUser, RecordStatusConstant.ACTIVE);
    }
}
