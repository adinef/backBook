package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer implements Serializable {

    @Transient
    private static final long serialVersionUID = 6262637406248131757L;

    @Id
    private String id;

    private String bookTitle;

    private String bookReleaseYear;

    private String bookPublisher;

    private String offerName;

    @DBRef
    private User offerOwner;

    @DBRef
    private Category category;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime expires;

    private Boolean active;

    private String city;

    private String voivodeship;

    private String fileId;
}
