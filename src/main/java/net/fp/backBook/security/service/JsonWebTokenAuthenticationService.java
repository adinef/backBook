package net.fp.backBook.security.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.TokenExpiredException;
import net.fp.backBook.model.User;
import net.fp.backBook.model.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class JsonWebTokenAuthenticationService implements TokenAuthenticationService {

    @Value("${security.token.secret.key}")
    private String secretKey;

    private UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    public JsonWebTokenAuthenticationService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {
        String token = this.resolveToken(request);
        Jws<Claims> tokenData = parseToken(token);
        if (tokenData != null) {
            if (this.getExpirationTimeFromToken(tokenData).isBefore(LocalDateTime.now())) {
                log.info("Token expired");
                throw new TokenExpiredException("Token expired");
            }
            try {
                User user = getUserFromToken(tokenData);
                return new UserAuthentication(user);
            } catch (UsernameNotFoundException e) {
                log.trace(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    private Jws<Claims> parseToken(String token) {
        if (token != null) {
            try {
                return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            } catch (SecurityException | MalformedJwtException e) {
                log.info("Invalid JWT signature.");
                log.trace("Invalid JWT signature trace: {}", e);
            } catch (ExpiredJwtException e) {
                log.info("Expired JWT token.");
                log.trace("Expired JWT token trace: {}", e);
            } catch (UnsupportedJwtException e) {
                log.info("Unsupported JWT token.");
                log.trace("Unsupported JWT token trace: {}", e);
            } catch (IllegalArgumentException e) {
                log.info("JWT token compact of handler are invalid.");
                log.trace("JWT token compact of handler are invalid trace: {}", e);
            }
        }
        return null;
    }

    private User getUserFromToken(Jws<Claims> tokenData) {
        return (User) userDetailsService
                .loadUserByUsername(tokenData.getBody().get("username").toString());
    }

    private LocalDateTime getExpirationTimeFromToken(Jws<Claims> tokenData) {
        return LocalDateTime.parse(tokenData.getBody().get("token_expiration_date").toString(), DateTimeFormatter.ISO_DATE_TIME);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
