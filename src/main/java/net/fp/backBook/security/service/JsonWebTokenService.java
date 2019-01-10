package net.fp.backBook.security.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AuthenticationException;
import net.fp.backBook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JsonWebTokenService implements TokenService {

    private static int tokenExpirationTime = 30;

    @Value("${security.token.secret.key}")
    private String tokenKey;

    private UserDetailsService userDetailsService;

    @Autowired
    public JsonWebTokenService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String getToken(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = (User) userDetailsService.loadUserByUsername(username);
        Map<String, Object> tokenData = new HashMap<>();
        if (password.equals(user.getPassword())) {
            tokenData.put("clientType", "user");
            tokenData.put("userID", user.getId());
            tokenData.put("username", user.getUsername());
            tokenData.put("authorities", user.getAuthorities());
            LocalDateTime createDate = LocalDateTime.now();
            tokenData.put("token_create_date", createDate);
            LocalDateTime expirationDate = createDate.plusMinutes(tokenExpirationTime);
            tokenData.put("token_expiration_date", expirationDate.format(DateTimeFormatter.ISO_DATE_TIME));
            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()));
            jwtBuilder.setClaims(tokenData);
            return jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenKey).compact();

        } else {
            throw new AuthenticationException("Authentication error");
        }
    }

    public static void setTokenExpirationTime(int tokenExpirationTime) {
        JsonWebTokenService.tokenExpirationTime = tokenExpirationTime;
    }
}
