package com.assessment.socialmedia.usecases.implementation;

import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.dao.FollowerEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.FollowerEntity;
import com.assessment.socialmedia.domain.entities.ResourceFileEntity;
import com.assessment.socialmedia.domain.entities.enums.CommonConstant;
import com.assessment.socialmedia.domain.entities.enums.RecordStatusConstant;
import com.assessment.socialmedia.infrastructure.security.AuthenticatedUser;
import com.assessment.socialmedia.infrastructure.security.JWTService;
import com.assessment.socialmedia.usecases.ResourceFileUseCase;
import com.assessment.socialmedia.usecases.UserManagementUseCase;
import com.assessment.socialmedia.usecases.data.request.LoginRequest;
import com.assessment.socialmedia.usecases.data.request.UserCreationRequest;
import com.assessment.socialmedia.usecases.data.response.*;
import com.assessment.socialmedia.usecases.exceptions.BadRequestException;
import com.assessment.socialmedia.utils.JStringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private final AppUserEntityDao appUserEntityDao;
    private final PasswordEncoder passwordEncoder;
    private final ResourceFileUseCase resourceFileUseCase;
    private final JWTService jwtService;
    private final FollowerEntityDao followerEntityDao;


    private static final int ACCESS_TOKEN_EXPIRY = 10;
    private static final int REFRESH_TOKEN_EXPIRY = 15;

    @Override
    public void createUser(UserCreationRequest request) {
        if (!JStringUtils.isEmailValid(request.getEmail())) {
            throw new BadRequestException("Invalid email.");
        }
        if (!JStringUtils.isAlphaNumericAndSpecialCharacter(request.getPassword())) {
            throw new BadRequestException("Password must contain alphanumeric and special characters.");
        }

        ResourceFileEntity resourceFile = resourceFileUseCase.createResourceFile(request.getProfilePicture());
        AppUserEntity appUser = AppUserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePicture(resourceFile)
                .build();
        appUserEntityDao.saveRecord(appUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), appUser.getPassword())) {
            throw new BadRequestException("Invalid password.");
        }
        return getAuthenticationResponse(appUser);
    }

    @Override
    public PagedDataResponse<UserResponse> getAllUsers(AuthenticatedUser authenticatedUser, int size, int page) {
        Page<AppUserEntity> entityPage = appUserEntityDao.getAllUsers(size, page);
        return new PagedDataResponse<>(entityPage.getTotalElements(), entityPage.getTotalPages(), entityPage.get().map(this::fromEntityToResponse).collect(Collectors.toList()));
    }

    @Override
    public UserResponse getUserByUsername(AuthenticatedUser authenticatedUser, String username) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(username);
        return fromEntityToResponse(appUser);
    }

    @Override
    public void deleteUser(AuthenticatedUser authenticatedUser) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        appUser.setRecordStatus(RecordStatusConstant.DELETED);
        appUserEntityDao.saveRecord(appUser);
    }

    @Override
    public void followUser(AuthenticatedUser authenticatedUser, String username) {
        AppUserEntity follower = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        AppUserEntity appUser = appUserEntityDao.getByUsername(username);
        FollowerEntity followerEntity = new FollowerEntity();
        followerEntity.setFollower(follower);
        followerEntity.setAppUser(appUser);
        followerEntityDao.saveRecord(followerEntity);
    }

    @Override
    public void unfollowUser(AuthenticatedUser authenticatedUser, String username) {
        AppUserEntity follower = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        AppUserEntity appUser = appUserEntityDao.getByUsername(username);
        Optional<FollowerEntity> followerEntityOpt = followerEntityDao.getByFollowerAndAppUser(follower, appUser);
        if (followerEntityOpt.isPresent()) {
            FollowerEntity followerEntity = followerEntityOpt.get();
            followerEntity.setRecordStatus(RecordStatusConstant.DELETED);
            followerEntityDao.saveRecord(followerEntity);
        }
    }

    @Override
    public void updateUser(AuthenticatedUser authenticatedUser, String email, MultipartFile profilePicture) {
        AppUserEntity appUser = appUserEntityDao.getByUsername(authenticatedUser.getUsername());
        if (StringUtils.isNotEmpty(email)) {
            if (!JStringUtils.isEmailValid(email)) {
                throw new BadRequestException("Invalid email.");
            }
            appUser.setEmail(email);
            appUserEntityDao.saveRecord(appUser);
        }
        if (profilePicture != null) {
            ResourceFileEntity resourceFile = appUser.getProfilePicture();
            resourceFileUseCase.updateProfilePicture(resourceFile, profilePicture);
        }

    }


    private UserResponse fromEntityToResponse(AppUserEntity appUserEntity) {
        ResourceFileEntity image = appUserEntity.getProfilePicture();
        return UserResponse.builder()
                .email(appUserEntity.getEmail())
                .username(appUserEntity.getUsername())
                .profilePicture(image.getResourceData())
                .build();
    }

    private LoginResponse getAuthenticationResponse(AppUserEntity appUser) {
        String authenticationKey = Base64.getEncoder().encodeToString(RandomStringUtils.randomAlphanumeric(15).getBytes());
        CustomerToken customerToken = generateToken(appUser, authenticationKey);

        LoginResponse response = new LoginResponse();
        response.setCustomerToken(customerToken);
        return response;
    }

    public CustomerToken generateToken(AppUserEntity appUser, String authenticationKey) {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("email", appUser.getEmail());
        tokenMap.put("username", appUser.getUsername());
        tokenMap.put("authKey", authenticationKey);
        tokenMap.put("tokenType", CommonConstant.ACCESS_TOKEN.name());

        String accessToken = jwtService.expiringToken(tokenMap, ACCESS_TOKEN_EXPIRY);

        tokenMap.replace("tokenType", CommonConstant.REFRESH_TOKEN.name());
        tokenMap.put("access_token_expiry_time", String.valueOf(ACCESS_TOKEN_EXPIRY));
        String refreshToken = jwtService.expiringToken(tokenMap, REFRESH_TOKEN_EXPIRY);
        return CustomerToken.builder()
                .accessToken(AppToken.builder()
                        .token(accessToken)
                        .expiryTimeInMinutes(ACCESS_TOKEN_EXPIRY)
                        .build())
                .refreshToken(AppToken.builder()
                        .token(refreshToken)
                        .expiryTimeInMinutes(REFRESH_TOKEN_EXPIRY)
                        .build())
                .build();
    }
}
