package com.assessment.socialmedia.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follower")
public class FollowerEntity extends AbstractBaseEntity<Long> {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AppUserEntity appUser;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AppUserEntity follower;
}
