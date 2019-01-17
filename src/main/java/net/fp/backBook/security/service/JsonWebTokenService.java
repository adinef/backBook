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

import java.util.Calendar;
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
        User user;
        try {
            user = (User) userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new AuthenticationException("Error during user details loading. " + e.getMessage());
        }
        Map<String, Object> tokenData = new HashMap<>();
        if (password.equals(user.getPassword())) {
            tokenData.put("userID", user.getId());
            tokenData.put("username", user.getUsername());
            tokenData.put("authorities", user.getAuthorities());
            JwtBuilder jwtBuilder = Jwts.builder();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, tokenExpirationTime);
            jwtBuilder.setClaims(tokenData);
            jwtBuilder.setExpiration(calendar.getTime());
            return jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenKey).compact();

        } else {
            throw new AuthenticationException("Authentication error");
        }
    }

    public static void setTokenExpirationTime(int tokenExpirationTime) {
        JsonWebTokenService.tokenExpirationTime = tokenExpirationTime;
    }

    public static int getTokenExpirationTime() {
        return JsonWebTokenService.tokenExpirationTime;
    }
}
