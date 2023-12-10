package com.assessment.socialmedia.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class CommentEntity extends AbstractBaseEntity<Long> {

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AppUserEntity creator;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PostEntity post;
}
