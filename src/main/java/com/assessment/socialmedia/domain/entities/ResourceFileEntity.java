package com.assessment.socialmedia.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.*;



@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_file")
public class ResourceFileEntity extends AbstractBaseEntity<Long> {

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String fileName;

    private long fileSizeInKb;

    @Lob
    private byte[] resourceData;

}
