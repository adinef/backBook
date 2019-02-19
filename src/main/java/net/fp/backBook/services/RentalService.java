package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface RentalService extends BasicCrudService<Rental, String> {

    @Secured({"ROLE_USER"})
    Rental getById(String id);

    @Secured({"ROLE_USER"})
    List<Rental> getAll();

    @Secured({"ROLE_USER"})
    void delete(String id);

    @Secured({"ROLE_USER"})
    Rental add(Rental offer);

    @Secured({"ROLE_USER"})
    Rental modify(Rental offer);

    @Secured({"ROLE_USER"})
    Rental getByOffer(Offer offer);

    @Secured({"ROLE_USER"})
    List<Rental> getAllByUser(User user);

    @Secured({"ROLE_USER"})
    Rental getByCounterOffer(CounterOffer counterOffer);

    @Secured({"ROLE_USER"})
    List<Rental> getAllByNotExpired(LocalDateTime startDate);
}
