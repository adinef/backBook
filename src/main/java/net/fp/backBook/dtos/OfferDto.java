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
public class OfferDto {

    private String id;

    private String bookTitle;

    private String bookReleaseYear;

    private String bookPublisher;

    private String offerName;

    //private String offerOwnerName;

    //private String offerOwnerLastName;

    private LocalDateTime createdAt;

    private LocalDateTime expires;

    private boolean active;

    private String city;

    private String voivodeship;
}
