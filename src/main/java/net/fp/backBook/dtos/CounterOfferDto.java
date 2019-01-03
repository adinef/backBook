package net.fp.backBook.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounterOfferDto {

    private String id;

    private OfferDto offer;

    private UserDto user;

    private LocalDateTime createdAt;

    private LocalDateTime expires;
}
