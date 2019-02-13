package net.fp.backBook.services;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface OfferService extends BasicCrudService<Offer, String> {

    Offer getById(String id);

    List<Offer> getAll();

    void delete(String id);

    Offer add(Offer offer);

    Offer modify(Offer offer);
    
    List<Offer> getAllByBookTitle(String title);

    List<Offer> getAllByOfferName(String title);

    List<Offer> getAllByBookPublisher(String bookPublisher);

    List<Offer> getAllCreatedBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<Offer> getAllNotExpired(LocalDateTime startDate);

    List<Offer> getAllByOfferOwner(User user);

    List<Offer> getAllByCity(String city);

    List<Offer> getAllByVoivodeship(String voivodeship);

    Page<Offer> getAllOffersByPage(int page, int limit);

    List<Offer> getByFilter(Offer offer);

    boolean existsByIdAndOfferOwner(String id, User user);
}
