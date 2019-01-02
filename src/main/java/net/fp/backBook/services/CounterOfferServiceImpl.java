package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.CounterOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CounterOfferServiceImpl implements  CounterOfferService {

    private CounterOfferRepository counterOfferRepository;

    @Autowired
    public CounterOfferServiceImpl(CounterOfferRepository counterOfferRepository) {
        this.counterOfferRepository = counterOfferRepository;
    }

    @Override
    public CounterOffer addCounterOffer(CounterOffer counterOffer) {
        return null;
    }

    @Override
    public CounterOffer modifyCounterOffer(CounterOffer counterOffer) {
        return null;
    }

    @Override
    public List<CounterOffer> getAllCounterOffers(Sort sort) {
        return null;
    }

    @Override
    public Optional<CounterOffer> getById(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteCounterOffer(String id) {

    }

    @Override
    public List<CounterOffer> getAllByOffer(Offer offer) {
        return null;
    }

    @Override
    public List<CounterOffer> getAllByUser(User user) {
        return null;
    }

    @Override
    public List<CounterOffer> getAllBetweenDates(LocalDateTime after, LocalDateTime before) {
        return null;
    }
}
