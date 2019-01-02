package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    private String id;

    private String bookTitle;

    private String bookReleaseYear;

    private String bookPublisher;

    private String offerName;

    @DBRef
    private User offerOwner;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime expires;

    private boolean active;

    private String city;

    private String voivodeship;
}
