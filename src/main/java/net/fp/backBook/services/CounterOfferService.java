package net.fp.backBook.services;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface CounterOfferService {

    CounterOffer addCounterOffer(CounterOffer counterOffer);

    CounterOffer modifyCounterOffer(CounterOffer counterOffer);

    List<CounterOffer> getAllCounterOffers();

    CounterOffer getById(String id);

    void deleteCounterOffer(String id);

    List<CounterOffer> getAllByOffer(Offer offer);

    List<CounterOffer> getAllByUser(User user);

    List<CounterOffer> getAllBetweenDates(LocalDateTime after, LocalDateTime before);
}
