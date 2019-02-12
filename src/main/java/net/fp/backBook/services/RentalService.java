package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface RentalService extends BasicCrudService<Rental, String> {

    Rental getById(String id);

    List<Rental> getAll();

    void delete(String id);

    Rental add(Rental offer);

    Rental modify(Rental offer);

    Rental getByOffer(Offer offer);

    List<Rental> getAllByUser(User user);

    Rental getByCounterOffer(CounterOffer counterOffer);

    List<Rental> getAllByNotExpired(LocalDateTime startDate);
}
