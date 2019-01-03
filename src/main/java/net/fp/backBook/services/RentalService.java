package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface RentalService {

    Rental addRental(Rental rental);

    Rental modifyRental(Rental rental);

    List<Rental> getAllRentals();

    Rental getById(String id);

    void deleteRental(String id);

    Rental getByOffer(Offer offer);

    List<Rental> getAllByUser(User user);

    Rental getByCounterOffer(CounterOffer counterOffer);

    List<Rental> getAllBetweenDates(LocalDateTime after, LocalDateTime before);
}
