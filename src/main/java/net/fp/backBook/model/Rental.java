package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Rental implements Serializable {

    @Transient
    private static final long serialVersionUID = 7947075393695654804L;

    @Id
    private String id;

    @DBRef
    private Offer offer;

    @DBRef
    private User user;

    @DBRef
    private CounterOffer counterOffer;

    private LocalDateTime startDate;

    private LocalDateTime expires;
}
