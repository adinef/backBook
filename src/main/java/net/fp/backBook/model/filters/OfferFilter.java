package net.fp.backBook.model.filters;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Component
public class OfferFilter {

    public ExampleMatcher getMatcher() {
        return ExampleMatcher
                .matching()
                .withMatcher("city", regex().ignoreCase())
                .withMatcher("voivodeship", regex().ignoreCase())
                .withMatcher("offerName", startsWith().ignoreCase())
                .withMatcher("bookTitle", regex().ignoreCase())
                .withMatcher("bookPublisher", regex().ignoreCase())
                .withMatcher("bookReleaseYear", startsWith())
                .withMatcher("category.name", regex().ignoreCase());
    }
}
