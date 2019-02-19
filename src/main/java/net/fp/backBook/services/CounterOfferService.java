package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface CounterOfferService extends BasicCrudService<CounterOffer, String> {

    @Secured({"ROLE_USER"})
    CounterOffer getById(String id);

    @Secured({"ROLE_USER"})
    List<CounterOffer> getAll();

    @Secured({"ROLE_USER"})
    void delete(String id);

    @Secured({"ROLE_USER"})
    CounterOffer add(CounterOffer offer);

    @Secured({"ROLE_USER"})
    CounterOffer modify(CounterOffer offer);

    @Secured({"ROLE_USER"})
    List<CounterOffer> getAllByOffer(Offer offer);

    @Secured({"ROLE_USER"})
    List<CounterOffer> getAllByUser(User user);

    @Secured({"ROLE_USER"})
    List<CounterOffer> getAllByNotExpired(LocalDateTime startDate);
}
