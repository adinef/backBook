package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RentalServiceImpl implements RentalService {

    private RentalRepository rentalRepository;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental add(Rental rental) {
        try {
            return this.rentalRepository.insert(rental);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e);
        }
    }

    @Override
    public Rental modify(Rental rental) {
        if(rental.getId() == null)
            throw new ModifyException("Id cannot be null for a rental to be modified.");
        try {
            return this.rentalRepository.save(rental);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ModifyException(e);
        }
    }

    @Override
    public List<Rental> getAll() {
        try {
            return this.rentalRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public Rental getById(String id) {
        try {
            return this.rentalRepository.findById(id)
                    .orElseThrow(() -> new GetException("Cannot find rental by id."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            this.rentalRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DeleteException(e);
        }
    }

    @Override
    public Rental getByOffer(Offer offer) {
        try {
            return this.rentalRepository.findByOffer(offer)
                    .orElseThrow(() -> new GetException("Cannot find rental by offer."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public List<Rental> getAllByUser(User user) {
        try {
            return this.rentalRepository.findAllByUser(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public Rental getByCounterOffer(CounterOffer counterOffer) {
        try {
            return this.rentalRepository.findByCounterOffer(counterOffer)
                    .orElseThrow(() -> new GetException("Cannot find rental by counter offer."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public List<Rental> getAllByNotExpired(LocalDateTime startDate) {
        try {
            return this.rentalRepository.findAllByNotExpired(startDate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }
}
