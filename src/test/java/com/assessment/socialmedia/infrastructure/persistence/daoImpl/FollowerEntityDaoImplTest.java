package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.FollowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerEntityDaoImplTest {

    @Mock
    private FollowerRepository mockRepository;

    private FollowerEntityDaoImpl followerEntityDaoImplUnderTest;

    @BeforeEach
    void setUp() {
        followerEntityDaoImplUnderTest = new FollowerEntityDaoImpl(mockRepository);
    }

    @Test
    void testGetByFollowerAndAppUser() {
        // Setup
        final AppUserEntity follower = AppUserEntity.builder().build();
        final AppUserEntity appUser = AppUserEntity.builder().build();
        final Optional<FollowerEntity> expectedResult = Optional.of(FollowerEntity.builder()
                .appUser(appUser)
                .follower(follower).build());

        // Configure FollowerRepository.findByFollowerAndAppUserAndRecordStatus(...).
        final Optional<FollowerEntity> followerEntity = Optional.of(FollowerEntity.builder().appUser(appUser)
                .follower(follower).build());
        when(mockRepository.findByFollowerAndAppUserAndRecordStatus(AppUserEntity.builder().build(),
                AppUserEntity.builder().build(), RecordStatusConstant.ACTIVE)).thenReturn(followerEntity);

        // Run the test
        final Optional<FollowerEntity> result = followerEntityDaoImplUnderTest.getByFollowerAndAppUser(follower,
                appUser);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetByFollowerAndAppUser_FollowerRepositoryReturnsAbsent() {
        // Setup
        final AppUserEntity follower = AppUserEntity.builder().build();
        final AppUserEntity appUser = AppUserEntity.builder().build();
        when(mockRepository.findByFollowerAndAppUserAndRecordStatus(AppUserEntity.builder().build(),
                AppUserEntity.builder().build(), RecordStatusConstant.ACTIVE)).thenReturn(Optional.empty());

        // Run the test
        final Optional<FollowerEntity> result = followerEntityDaoImplUnderTest.getByFollowerAndAppUser(follower,
                appUser);

        // Verify the results
        assertThat(result).isEmpty();
    }
}
