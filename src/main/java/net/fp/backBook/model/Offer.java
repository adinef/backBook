package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity //not neccessary for mongo
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    private int id;

    private String bookTitle;

    private String bookReleaseYear;

    private String bookPublisher;

    private String offerName;

    private User offerOwner;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime expires;

    private boolean active;

    private String city;

    private String voivodeship;
}
