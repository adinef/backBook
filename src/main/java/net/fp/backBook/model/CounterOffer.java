package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CounterOffer implements Serializable {

    @Transient
    private static final long serialVersionUID = 2679514265637395183L;

    @Id
    private String id;

    @DBRef
    private Offer offer;

    @DBRef
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime expires;
}
