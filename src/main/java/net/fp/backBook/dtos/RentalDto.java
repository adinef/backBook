package net.fp.backBook.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fp.backBook.model.CounterOffer;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalDto {

    private String id;

    private OfferDto offer;

    private UserDto user;

    private CounterOfferDto counterOffer;

    private LocalDateTime startDate;

    private LocalDateTime expires;
}
