package net.fp.backBook.repositories;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends MongoRepository<Rental, String> {

    Rental findByOffer(Offer offer);

    @Query(value = "{'expires': {$gte: ?0, $lte: ?1}}")
    List<Rental> findAllByExpiresBeetweenOrEquals(LocalDateTime after, LocalDateTime before);
}