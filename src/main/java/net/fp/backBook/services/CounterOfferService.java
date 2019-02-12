package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface CounterOfferService extends BasicCrudService<CounterOffer, String> {

    CounterOffer getById(String id);

    List<CounterOffer> getAll();

    void delete(String id);

    CounterOffer add(CounterOffer offer);

    CounterOffer modify(CounterOffer offer);

    List<CounterOffer> getAllByOffer(Offer offer);

    List<CounterOffer> getAllByUser(User user);

    List<CounterOffer> getAllByNotExpired(LocalDateTime startDate);
}
