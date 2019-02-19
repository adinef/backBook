package net.fp.backBook.repositories;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {
    List<Offer> findAllByOfferName(String name);
    List<Offer> findAllByOfferOwner(User offerOwner);
    @Query("{'expires' : { $gte: ?0, $lte: ?1 } }")
    List<Offer> findAllByExpiresBetween(LocalDateTime starteDate, LocalDateTime endDate);
    @Query("{'expires' : { $gte: ?0} }")
    List<Offer> findAllByNotExpired(LocalDateTime starteDate);
    List<Offer> findAllByBookTitle(String bookTitle);
    List<Offer> findAllByBookPublisher(String bookPublisher);
    List<Offer> findAllByCity(String city);
    List<Offer> findAllByVoivodeship(String voivodeship);
    boolean existsByIdAndOfferOwner(String id, User user);
    Page<Offer> findAllByOfferOwner(User offerOwner, Pageable pageable);
}
