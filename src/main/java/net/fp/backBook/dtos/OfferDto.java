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
public class OfferDto {

    private String id;

    private String bookTitle;

    private String bookReleaseYear;

    private String bookPublisher;

    private String offerName;

    private UserDto offerOwner;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expires;

    private Boolean active;

    private String city;

    private String voivodeship;
}
