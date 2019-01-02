package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RentalServiceImpl implements RentalService {

    private RentalRepository rentalRepository;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental addRental(Rental rental) {
        return null;
    }

    @Override
    public Rental modifyRental(Rental rental) {
        return null;
    }

    @Override
    public List<Rental> getAllRentals(Sort sort) {
        return null;
    }

    @Override
    public Optional<Rental> getById(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteRental(String id) {

    }

    @Override
    public List<Rental> getAllByOffer(Offer offer) {
        return null;
    }

    @Override
    public List<Rental> getAllBetweenDates(LocalDateTime after, LocalDateTime before) {
        return null;
    }
}
