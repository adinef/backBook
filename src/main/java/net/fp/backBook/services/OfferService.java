package net.fp.backBook.services;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface OfferService extends BasicCrudService<Offer, String> {
    @Secured({"ROLE_USER"})
    Offer getById(String id);

    @Secured({"ROLE_USER"})
    List<Offer> getAll();

    @Secured({"ROLE_USER"})
    void delete(String id);

    @Secured({"ROLE_USER"})
    Offer add(Offer offer);

    @Secured({"ROLE_USER"})
    Offer modify(Offer offer);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByBookTitle(String title);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByOfferName(String title);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByBookPublisher(String bookPublisher);

    @Secured({"ROLE_USER"})
    List<Offer> getAllNotExpired(LocalDateTime startDate);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByOfferOwner(User user);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByCity(String city);

    @Secured({"ROLE_USER"})
    List<Offer> getAllByVoivodeship(String voivodeship);

    @Secured({"ROLE_USER"})
    Page<Offer> getAllOffersByPage(int page, int limit);

    @Secured({"ROLE_USER"})
    List<Offer> getByFilter(Offer offer);

    @Secured({"ROLE_USER"})
    boolean existsByIdAndOfferOwner(String id, User user);

    @Secured({"ROLE_USER"})
    Page<Offer> getAllUsersOffersByPage(User user, int page, int limit);

    @Secured({"ROLE_USER"})
    Page<Offer> getPageByFilter(Offer searchCriteriaOffer, int page, int limit);
}
