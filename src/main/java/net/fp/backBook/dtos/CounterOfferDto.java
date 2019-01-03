package net.fp.backBook.dtos;

import java.time.LocalDateTime;

public class CounterOfferDto {

    private String id;

    private OfferDto offer;

    private UserDto user;

    private LocalDateTime createdAt;

    private LocalDateTime expires;
}
