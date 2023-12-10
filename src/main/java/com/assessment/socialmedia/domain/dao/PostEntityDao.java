package com.assessment.socialmedia.domain.dao;

import com.assessment.socialmedia.domain.entities.PostEntity;
import org.springframework.data.domain.Page;

public interface PostEntityDao extends CrudDao<PostEntity, Long> {
    Page<PostEntity> getAllPosts(int size, int page);
}
