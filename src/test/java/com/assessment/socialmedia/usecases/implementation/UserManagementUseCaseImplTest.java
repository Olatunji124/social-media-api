package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.FollowerEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;
import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import com.assessment.socialmedia.domain.entities.enums.CommonConstant;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.infrastructure.security.JWTService;
import com.assessment.socialmedia.usecases.ResourceFileUseCase;
import com.assessment.socialmedia.usecases.data.request.LoginRequest;
import com.assessment.socialmedia.usecases.data.request.UserCreationRequest;
import com.assessment.socialmedia.usecases.data.response.*;
import com.assessment.socialmedia.usecases.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserManagementUseCaseImplTest {

    @Mock
    private AppUserEntityDao mockAppUserEntityDao;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private ResourceFileUseCase mockResourceFileUseCase;
    @Mock
    private JWTService mockJwtService;
    @Mock
    private FollowerEntityDao mockFollowerEntityDao;

    private UserManagementUseCaseImpl userManagementUseCaseImplUnderTest;

    @BeforeEach
    void setUp() {
        userManagementUseCaseImplUnderTest = new UserManagementUseCaseImpl(mockAppUserEntityDao, mockPasswordEncoder,
                mockResourceFileUseCase, mockJwtService, mockFollowerEntityDao);
    }

    @Test
    void testCreateUser() {
        // Setup
        final UserCreationRequest request = new UserCreationRequest();
        request.setUsername("username");
        request.setPassword("password23@#");
        request.setEmail("email@gmail.com");
        request.setProfilePicture(new MockMultipartFile("name", new byte[100]));

        // Configure ResourceFileUseCase.createResourceFile(...).
        final ResourceFileEntity resourceFile = ResourceFileEntity.builder()
                .resourceData(new byte[100])
                .build();
        when(mockResourceFileUseCase.createResourceFile(any(MultipartFile.class))).thenReturn(resourceFile);

        when(mockPasswordEncoder.encode("password23@#")).thenReturn("password");

        // Run the test
        userManagementUseCaseImplUnderTest.createUser(request);

        // Verify the results
        verify(mockAppUserEntityDao).saveRecord(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(resourceFile)
                .build());
    }

    @Test
    void testLogin() {
        // Setup
        final LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password23@#");

        final LoginResponse expectedResult = new LoginResponse();
        expectedResult.setCustomerToken(CustomerToken.builder()
                .accessToken(AppToken.builder()
                        .token("token")
                        .expiryTimeInMinutes(10)
                        .build())
                .refreshToken(AppToken.builder()
                        .token("token")
                        .expiryTimeInMinutes(15)
                        .build())
                .build());

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email@gmail.com")
                .password("password23@#")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData(new byte[100])
                        .build())
                .build();
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("email", appUser.getEmail());
        tokenMap.put("username", appUser.getUsername());
        tokenMap.put("authKey", "authenticationKey");
        tokenMap.put("tokenType", CommonConstant.ACCESS_TOKEN.name());

        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);
        when(mockPasswordEncoder.matches("password23@#", "password23@#")).thenReturn(true);
        when(mockJwtService.expiringToken(tokenMap, 10)).thenReturn("token");

        // Run the test
        final LoginResponse result = userManagementUseCaseImplUnderTest.login(request);

        // Verify the results
        assertThat(result.getCustomerToken()).isNotNull();
    }

    @Test
    void testLogin_PasswordEncoderReturnsFalse() {
        // Setup
        final LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password");

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        when(mockPasswordEncoder.matches("password", "password")).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> userManagementUseCaseImplUnderTest.login(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetAllUsers() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<UserResponse> expectedResult = new PagedDataResponse<>(1L, 1L,
                List.of(UserResponse.builder()
                        .username("username")
                        .email("email")
                        .profilePicture("content".getBytes())
                        .build()));

        // Configure AppUserEntityDao.getAllUsers(...).
        final Page<AppUserEntity> appUserEntities = new PageImpl<>(List.of(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData(new byte[100])
                        .build())
                .build()));
        when(mockAppUserEntityDao.getAllUsers(10, 0)).thenReturn(appUserEntities);

        // Run the test
        //final PagedDataResponse<UserResponse> result = userManagementUseCaseImplUnderTest.getAllUsers(authenticatedUser,10, 0);

    }

    @Test
    void testGetAllUsers_AppUserEntityDaoReturnsNoItems() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final PagedDataResponse<UserResponse> expectedResult = new PagedDataResponse<>(0L, 1L,
                List.of());
        when(mockAppUserEntityDao.getAllUsers(0, 0)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final PagedDataResponse<UserResponse> result = userManagementUseCaseImplUnderTest.getAllUsers(authenticatedUser,
                0, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUserByUsername() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final UserResponse expectedResult = UserResponse.builder()
                .username("username")
                .email("email")
                .profilePicture("content".getBytes())
                .build();

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        // Run the test
        //final UserResponse result = userManagementUseCaseImplUnderTest.getUserByUsername(authenticatedUser, "username");

        // Verify the results
        //assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testDeleteUser() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        // Run the test
        userManagementUseCaseImplUnderTest.deleteUser(authenticatedUser);

        // Verify the results
        verify(mockAppUserEntityDao).saveRecord(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build());
    }

    @Test
    void testFollowUser() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        // Run the test
        userManagementUseCaseImplUnderTest.followUser(authenticatedUser, "username");

        // Verify the results
        verify(mockFollowerEntityDao).saveRecord(FollowerEntity.builder()
                .appUser(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .follower(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .build());
    }

    @Test
    void testUnfollowUser() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        // Configure FollowerEntityDao.getByFollowerAndAppUser(...).
        final Optional<FollowerEntity> followerEntity = Optional.of(FollowerEntity.builder()
                .appUser(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .follower(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .build());
        when(mockFollowerEntityDao.getByFollowerAndAppUser(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build(), AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build())).thenReturn(followerEntity);

        // Run the test
        userManagementUseCaseImplUnderTest.unfollowUser(authenticatedUser, "username");

        // Verify the results
        verify(mockFollowerEntityDao).saveRecord(FollowerEntity.builder()
                .appUser(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .follower(AppUserEntity.builder()
                        .username("username")
                        .email("email")
                        .password("password")
                        .profilePicture(ResourceFileEntity.builder()
                                .resourceData("content".getBytes())
                                .build())
                        .build())
                .build());
    }

    @Test
    void testUnfollowUser_FollowerEntityDaoGetByFollowerAndAppUserReturnsAbsent() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        when(mockFollowerEntityDao.getByFollowerAndAppUser(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build(), AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build())).thenReturn(Optional.empty());

        // Run the test
        userManagementUseCaseImplUnderTest.unfollowUser(authenticatedUser, "username");

        // Verify the results
    }

    @Test
    void testUpdateUser() {
        // Setup
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(0L);
        authenticatedUser.setUsername("username");
        authenticatedUser.setPassword("password");
        authenticatedUser.setEnabled(false);
        authenticatedUser.setBlocked(false);

        final MultipartFile profilePicture = new MockMultipartFile("name", "content".getBytes());

        // Configure AppUserEntityDao.getByUsername(...).
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email@gmail.com")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        when(mockAppUserEntityDao.getByUsername("username")).thenReturn(appUser);

        // Run the test
        userManagementUseCaseImplUnderTest.updateUser(authenticatedUser, "email@gmail.com", profilePicture);

        // Verify the results
        verify(mockAppUserEntityDao).saveRecord(AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build());
        verify(mockResourceFileUseCase).updateProfilePicture(eq(ResourceFileEntity.builder()
                .resourceData("content".getBytes())
                .build()), any(MultipartFile.class));
    }

    @Test
    void testGenerateToken() {
        // Setup
        final AppUserEntity appUser = AppUserEntity.builder()
                .username("username")
                .email("email")
                .password("password")
                .profilePicture(ResourceFileEntity.builder()
                        .resourceData("content".getBytes())
                        .build())
                .build();
        final CustomerToken expectedResult = CustomerToken.builder()
                .accessToken(AppToken.builder()
                        .token("token")
                        .expiryTimeInMinutes(10)
                        .build())
                .refreshToken(AppToken.builder()
                        .token("token")
                        .expiryTimeInMinutes(15)
                        .build())
                .build();
        when(mockJwtService.expiringToken(Map.ofEntries(Map.entry("value", "value")), 10)).thenReturn("token");

        // Run the test
        final CustomerToken result = userManagementUseCaseImplUnderTest.generateToken(appUser, "authenticationKey");

        // Verify the results
        assertThat(result).isNotNull();
    }
}
