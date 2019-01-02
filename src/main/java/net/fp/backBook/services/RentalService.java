package net.fp.backBook.services;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface RentalService {

    Rental addRental(Rental rental);

    Rental modifyRental(Rental rental);

    List<Rental> getAllRentals(Sort sort);

    Optional<Rental> getById(String id);

    void deleteRental(String id);

    List<Rental> getAllByOffer(Offer offer);

    List<Rental> getAllBetweenDates(LocalDateTime after, LocalDateTime before);
}
