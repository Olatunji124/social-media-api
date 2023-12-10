package com.assessment.socialmedia.infrastructure.security;

import com.assessment.socialmedia.infrastructure.model.ApiResponseJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
			new AntPathRequestMatcher("/*"),
			new AntPathRequestMatcher("/api/v1/signup/**"));
	private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

	private final JwtTokenProvider jwtTokenProvider;

	public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement((session) ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.cors((config) ->
						config.configurationSource(corsConfigurationSource()))
				.exceptionHandling((exception) ->
						exception.defaultAuthenticationEntryPointFor(restAuthenticationEntryPoint(), PROTECTED_URLS))
				.authenticationProvider(jwtTokenProvider)
				.authorizeHttpRequests(request -> request
						.requestMatchers(PROTECTED_URLS).authenticated().anyRequest().permitAll())
				.addFilterBefore(restAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))), AnonymousAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PUBLIC_URLS);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}


	public JwtTokenFilter restAuthenticationFilter(AuthenticationManager authenticationManager) {
		final JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(PROTECTED_URLS, authenticationManager);
		jwtTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
		jwtTokenFilter.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
		return jwtTokenFilter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "Cache-Control", "Content-Type", "client-key"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new HttpStatusEntryPoint(FORBIDDEN);
	}

	private static class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
											Authentication authentication) {
		}
	}

	private static class JwtAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException exception) throws IOException {
			ApiResponseJSON<String> apiResponse = new ApiResponseJSON<>(exception.getMessage());
			response.setHeader("Content-Type", "application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getOutputStream().write(new ObjectMapper().writeValueAsString(apiResponse).getBytes());
		}
	}
}
