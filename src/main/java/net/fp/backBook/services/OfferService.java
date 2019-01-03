package net.fp.backBook.services;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface OfferService {
    Offer getById(String id);
    List<Offer> getAllOffers();
    void deleteOffer(String id);
    Offer addOffer(Offer offer);
    Offer modifyOffer(Offer offer);
    List<Offer> getAllByBookTitle(String title);
    List<Offer> getAllByBookPublisher(String bookPublisher);
    List<Offer> getAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    List<Offer> getAllByOfferOwner(User user);
    List<Offer> getAllByCity(String city);
    List<Offer> getAllByVoivodeship(String voivodeship);
    List<Offer> getByFilter(Offer offer);
}
