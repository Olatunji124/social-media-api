package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.persistence.repository.AppUserRepository;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserEntityDaoImplTest {

    @Mock
    private AppUserRepository mockRepository;

    private AppUserEntityDaoImpl appUserEntityDaoImplUnderTest;

    @BeforeEach
    void setUp() {
        appUserEntityDaoImplUnderTest = new AppUserEntityDaoImpl(mockRepository);
    }

    @Test
    void testFindByUsername() {
        // Setup
        String username = "olatunji124";
        final Optional<AppUserEntity> expectedResult = Optional.of(AppUserEntity.builder()
                        .username(username)
                .build());

        // Configure AppUserRepository.findFirstByUsernameAndRecordStatus(...).
        final Optional<AppUserEntity> appUserEntity = Optional.of(AppUserEntity.builder().username(username).build());
        when(mockRepository.findFirstByUsernameAndRecordStatus(username, RecordStatusConstant.ACTIVE))
                .thenReturn(appUserEntity);

        // Run the test
        final Optional<AppUserEntity> result = appUserEntityDaoImplUnderTest.findByUsername(username);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindByUsername_AppUserRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findFirstByUsernameAndRecordStatus("username", RecordStatusConstant.ACTIVE))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<AppUserEntity> result = appUserEntityDaoImplUnderTest.findByUsername("username");

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testGetByUsername() {
        // Setup
        final AppUserEntity expectedResult = AppUserEntity.builder().build();

        // Configure AppUserRepository.findFirstByUsernameAndRecordStatus(...).
        final Optional<AppUserEntity> appUserEntity = Optional.of(AppUserEntity.builder().build());
        when(mockRepository.findFirstByUsernameAndRecordStatus("username", RecordStatusConstant.ACTIVE))
                .thenReturn(appUserEntity);

        // Run the test
        final AppUserEntity result = appUserEntityDaoImplUnderTest.getByUsername("username");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetByUsername_AppUserRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findFirstByUsernameAndRecordStatus("username", RecordStatusConstant.ACTIVE))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appUserEntityDaoImplUnderTest.getByUsername("username"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testExistsByEmail() {
        // Setup
        when(mockRepository.existsByEmailAndRecordStatus("email", RecordStatusConstant.ACTIVE)).thenReturn(false);

        // Run the test
        final boolean result = appUserEntityDaoImplUnderTest.existsByEmail("email");

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testExistsByEmail_AppUserRepositoryReturnsTrue() {
        // Setup
        when(mockRepository.existsByEmailAndRecordStatus("email", RecordStatusConstant.ACTIVE)).thenReturn(true);

        // Run the test
        final boolean result = appUserEntityDaoImplUnderTest.existsByEmail("email");

        // Verify the results
        assertThat(result).isTrue();
    }

    @Test
    void testGetAllUsers() {

        // Configure AppUserRepository.findAll(...).
        final Page<AppUserEntity> appUserEntities = new PageImpl<>(List.of(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password").build()));
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(appUserEntities);

        // Run the test
        final Page<AppUserEntity> result = appUserEntityDaoImplUnderTest.getAllUsers(10, 0);

        // Verify the results
        assertThat(result).isEqualTo(appUserEntities);
    }

    @Test
    void testGetAllUsers_AppUserRepositoryReturnsNoItems() {
        // Setup
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<AppUserEntity> result = appUserEntityDaoImplUnderTest.getAllUsers(10, 0);

        // Verify the results
        assertThat(result).isEmpty();
    }
}
