package com.assessment.socialmedia.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class AppUserEntity extends AbstractBaseEntity<Long> {

    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private ResourceFileEntity profilePicture;
}
