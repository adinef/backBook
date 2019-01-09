package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.CounterOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CounterOfferServiceImpl implements CounterOfferService {

    private CounterOfferRepository counterOfferRepository;

    @Autowired
    public CounterOfferServiceImpl(CounterOfferRepository counterOfferRepository) {
        this.counterOfferRepository = counterOfferRepository;
    }

    @Override
    public CounterOffer add(CounterOffer counterOffer) {
        try {
            return this.counterOfferRepository.insert(counterOffer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e);
        }
    }

    @Override
    public CounterOffer modify(CounterOffer counterOffer) {
        if(counterOffer.getId() == null)
            throw new ModifyException("Id cannot be null for a counterOffer to be modified.");
        try {
            return this.counterOfferRepository.save(counterOffer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ModifyException(e);
        }
    }

    @Override
    public List<CounterOffer> getAll() {
        try {
            return this.counterOfferRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public CounterOffer getById(String id) {
        try {
            return this.counterOfferRepository.findById(id)
                    .orElseThrow(() -> new GetException("Cannot find counter offer by id."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            this.counterOfferRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DeleteException(e);
        }
    }

    @Override
    public List<CounterOffer> getAllByOffer(Offer offer) {
        try {
            return this.counterOfferRepository.findByOffer(offer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public List<CounterOffer> getAllByUser(User user) {
        try {
            return this.counterOfferRepository.findByUser(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public List<CounterOffer> getAllByNotExpired(LocalDateTime startDate) {
        try {
            return this.counterOfferRepository.findAllByNotExpired(startDate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }
}
