package com.assessment.socialmedia.infrastructure.security;


import com.assessment.socialmedia.domain.dao.AppUserEntityDao;
import com.assessment.socialmedia.domain.entities.AppUserEntity;
import com.assessment.socialmedia.domain.entities.enums.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
@Component
public class JwtTokenProvider extends AbstractUserDetailsAuthenticationProvider {

    private JWTService jwtService;
    private AppUserEntityDao appUserEntityDao;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String token = String.valueOf(authentication.getCredentials());
        return findByToken(token).orElseThrow(() -> new UsernameNotFoundException("Invalid authorization token."));
    }

    private Optional<AuthenticatedUser> findByToken(String token) {
        Map<String, String> attributes = jwtService.verify(token);
        if (attributes == null || attributes.isEmpty()) {
            return Optional.empty();
        }

        final String username = attributes.get("username");
        final String authKey = attributes.get("authKey");
        final String email = attributes.get("email");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(authKey) || StringUtils.isEmpty(email)) {
            return Optional.empty();
        }
        String tokenType = attributes.getOrDefault("tokenType", "");
        if (!CommonConstant.ACCESS_TOKEN.name().equalsIgnoreCase(tokenType)) {
            return Optional.empty();
        }
        Optional<AppUserEntity> appUserOptional = appUserEntityDao.findByUsername(username);
        if (appUserOptional.isPresent()) {
            AppUserEntity appUser = appUserOptional.get();
            return Optional.of(fromAppUserToUserDetail(appUser));
        }
        return Optional.empty();
    }

    private AuthenticatedUser fromAppUserToUserDetail(AppUserEntity appUser) {
        AuthenticatedUser userDetails = new AuthenticatedUser();
        userDetails.setId(appUser.getId());
        userDetails.setPassword(appUser.getPassword());
        userDetails.setUsername(appUser.getUsername());
        userDetails.setEnabled(true);
        userDetails.setBlocked(false);
        userDetails.setEmail(appUser.getEmail());
        userDetails.setDeactivated(false);
        userDetails.setExpiredCredential(false);
        return userDetails;
    }
}
