package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.dao.ResourceFileEntityDao;
import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import com.assessment.socialmedia.infrastructure.persistence.repository.ResourceFileRepository;
import org.springframework.stereotype.Service;

@Service
public class ResourceFileEntityDaoImpl extends CrudDaoImpl<ResourceFileEntity, Long> implements ResourceFileEntityDao {

    private final ResourceFileRepository repository;
    public ResourceFileEntityDaoImpl(ResourceFileRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
