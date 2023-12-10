package com.assessment.socialmedia.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
public class JwtTokenFilter extends AbstractAuthenticationProcessingFilter {

    public JwtTokenFilter(final RequestMatcher protectedRoutes, AuthenticationManager authenticationManager) {
        super(protectedRoutes, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Optional<String> paramOptional = ofNullable(request.getHeader(AUTHORIZATION));
        if(paramOptional.isEmpty() || !paramOptional.get().toLowerCase().startsWith("bearer ")) {
            throw new BadCredentialsException("Bad Authorization Token format.");
        }
        final String token = paramOptional.get().substring(7);
        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationDetailsSource.buildDetails(request), token);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
