package net.fp.backBook.repositories;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends MongoRepository<Rental, String> {

    Optional<Rental> findByOffer(Offer offer);

    List<Rental> findAllByUser(User user);

    Optional<Rental> findByCounterOffer(CounterOffer counterOffer);

    @Query(value = "{'expires': {$gte: ?0, $lte: ?1}}")
    List<Rental> findAllByExpiresBeetweenOrEquals(LocalDateTime after, LocalDateTime before);
}
