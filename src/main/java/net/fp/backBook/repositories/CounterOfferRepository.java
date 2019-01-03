package net.fp.backBook.repositories;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounterOfferRepository extends MongoRepository<CounterOffer, String> {

    List<CounterOffer> findByOffer(Offer offer);

    List<CounterOffer> findByUser(User user);

    @Query("{'expires' : { $gte: ?0} }")
    List<CounterOffer> findAllByNotExpired(LocalDateTime startDate);
}
