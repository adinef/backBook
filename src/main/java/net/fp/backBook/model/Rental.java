package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

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

    private Offer offer;

    private LocalDateTime startDate;

    private LocalDateTime expires;
}
