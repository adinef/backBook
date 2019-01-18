package net.fp.backBook.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferSearchFilter {
    private String city;
    private String voivodeship;
    private String offerName;
    private String bookTitle;
    private String bookPublisher;
    private String bookReleaseYear;
    private CategoryDto category;
}
