package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.entities.CommentEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.CommentRepository;
import com.assessment.socialmedia.usecases.CommentEntityDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CommentEntityDaoImpl extends CrudDaoImpl<CommentEntity, Long> implements CommentEntityDao {

    private final CommentRepository repository;
    public CommentEntityDaoImpl(CommentRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Page<CommentEntity> getAllComments(PostEntity post, int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Specification<CommentEntity> specification = Specification.where(withRecordStatus());
        specification = specification.and(withPost(post));
        return repository.findAll(specification, pageable);
    }

    private Specification<CommentEntity> withPost(PostEntity post) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("post"), post);
    }

    private Specification<CommentEntity> withRecordStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("recordStatus"), RecordStatusConstant.ACTIVE);
    }
}
