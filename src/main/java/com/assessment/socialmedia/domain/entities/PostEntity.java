package com.assessment.socialmedia.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class PostEntity extends AbstractBaseEntity<Long> {

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AppUserEntity appUser;

    private int likes;
}
