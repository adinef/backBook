package net.fp.backBook.repositories;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {
    Optional<Offer> findByOfferName(String name);
    List<Offer> findAllByUser(User user);
    @Query("{'expires' : { $gte: ?0, $lte: ?1 } }")
    List<Offer> findAllByExpiresBetween(LocalDateTime starteDate, LocalDateTime endDate);
    @Query("{'createdAt' : { $gte: ?0, $lte: ?1 } }")
    List<Offer> findAllByCreatedAtBetween(LocalDateTime starteDate, LocalDateTime endDate);
}
