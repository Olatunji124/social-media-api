package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.AppUserRepository;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import jakarta.inject.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;


@Named
public class AppUserEntityDaoImpl  extends CrudDaoImpl<AppUserEntity, Long> implements AppUserEntityDao {

    private final AppUserRepository repository;

    public AppUserEntityDaoImpl(AppUserRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Override
    public Optional<AppUserEntity> findByUsername(String username) {
        return repository.findFirstByUsernameAndRecordStatus(username, RecordStatusConstant.ACTIVE);
    }

    @Override
    public AppUserEntity getByUsername(String username) {
        return findByUsername(username).orElseThrow(() -> new NotFoundException("Not found. User with Username: "+username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailAndRecordStatus(email, RecordStatusConstant.ACTIVE);
    }

    @Override
    public Page<AppUserEntity> getAllUsers(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Specification<AppUserEntity> specification = Specification.where(withRecordStatus());
        return repository.findAll(specification, pageable);
    }

    private Specification<AppUserEntity> withRecordStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("recordStatus"), RecordStatusConstant.ACTIVE);
    }

}
