package com.assessment.socialmedia.infrastructure.security;


import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${token.issuer:social}")
    private String issuer;
    @Value("${token.secret:12345678901234567890123456789012}")
    private String secretKey;
    @Value("${token.clock-skew-sec:300}")
    private int clockSkewSec;

    private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

    @Override
    public String expiringToken(Map<String, String> attributes, int minutes) {
        return newToken(attributes, minutes);
    }

    @Override
    public Map<String, String> verify(String tokenString) {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setSigningKey(secretKey);
        return parseClaims(() -> parser.parseClaimsJws(tokenString).getBody());
    }

    private String newToken(Map<String, String> attributes, int minutes) {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        LocalDateTime now = LocalDateTime.now();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        if (minutes > 0) {
            final LocalDateTime expiresAt = now.plusMinutes(minutes);
            claims.setExpiration(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        claims.putAll(attributes);
        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(HS256, secretKey)
                .compressWith(COMPRESSION_CODEC)
                .compact();
    }

    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (final Map.Entry<String, Object> e: claims.entrySet()) {
                builder.put(e.getKey(), String.valueOf(e.getValue()));
            }
            return builder.build();
        } catch (final IllegalArgumentException | JwtException e) {
            return ImmutableMap.of();
        }
    }
}
