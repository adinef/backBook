package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken implements Serializable {
    @Value("net.fp.backBook.email_token_exp")
    private static final int EXPIRATION = 0;

    @Id
    private String id;

    private String token;

    @DBRef
    User user;

    private LocalDateTime expires;

    private LocalDateTime recalculateExpiryTime(int expiryTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        expires = currentTime.plusMinutes(expiryTime);
        return expires;
    }
}
