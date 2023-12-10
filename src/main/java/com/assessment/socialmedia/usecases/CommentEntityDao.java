package com.assessment.socialmedia.usecases;

import com.assessment.socialmedia.domain.dao.CrudDao;
import com.assessment.socialmedia.domain.entities.CommentEntity;
import com.assessment.socialmedia.domain.entities.PostEntity;
import org.springframework.data.domain.Page;

public interface CommentEntityDao extends CrudDao<CommentEntity, Long> {
    Page<CommentEntity> getAllComments(PostEntity post, int size, int page);
}
