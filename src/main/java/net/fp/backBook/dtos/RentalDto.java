package net.fp.backBook.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalDto {

    private String id;

    private OfferDto offer;

    private UserViewDto user;

    private CounterOfferDto counterOffer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expires;
}
