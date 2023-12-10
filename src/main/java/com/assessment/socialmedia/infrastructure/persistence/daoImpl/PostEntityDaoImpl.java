package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.dao.PostEntityDao;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
@Service
public class PostEntityDaoImpl extends CrudDaoImpl<PostEntity, Long> implements PostEntityDao {

    private final PostRepository repository;
    public PostEntityDaoImpl(PostRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Page<PostEntity> getAllPosts(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Specification<PostEntity> specification = Specification.where(withRecordStatus());
        return repository.findAll(specification, pageable);
    }

    private Specification<PostEntity> withRecordStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("recordStatus"), RecordStatusConstant.ACTIVE);
    }
}
