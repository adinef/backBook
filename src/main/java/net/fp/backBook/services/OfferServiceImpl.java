package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Slf4j
@Service
public class OfferServiceImpl implements OfferService {

    private OfferRepository offerRepository;

    @Autowired
    public OfferServiceImpl(final OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public Offer getOfferById(String id) {
        try {
            return offerRepository.findById(id).orElseThrow( () -> new GetException("Cannot find offer by id."));
        } catch (final Exception e) {
            log.error("Error during getting Offer object, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllOffers() {
        try {
            return offerRepository.findAll();
        } catch (final Exception e) {
            log.error("Error during getting Offer objects, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public void deleteOffer(String id) {
        try {
            offerRepository.deleteById(id);
        } catch (final Exception e) {
            log.error("Error during deleting Offer object by id, {}", e);
            throw new DeleteException(e.getMessage());
        }
    }

    @Override
    public Offer addOffer(Offer offer) {
        try {
            offer.setCreatedAt(LocalDateTime.now());
            offerRepository.insert(offer);
        } catch (final Exception e) {
            log.error("Error during inserting Offer object, {}", e);
            throw new AddException(e.getMessage());
        }
        return offer;
    }

    @Override
    public Offer modifyOffer(Offer offer) {
        if(offer.getId() == null)
            throw new ModifyException("Id cannot be null for offer to be modified.");
        try {
            offerRepository.save(offer);
        } catch (final Exception e) {
            log.error("Error during saving Offer object, {}", e);
            throw new ModifyException(e.getMessage());
        }
        return offer;
    }

    @Override
    public List<Offer> getAllByBookTitle(String bookTitle) {
        try {
            return offerRepository.findAllByBookTitle(bookTitle);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by title, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllByOfferName(String name) {
        try {
            return offerRepository.findAllByOfferName(name);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by offer name, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllByBookPublisher(String bookPublisher) {
        try {
            return offerRepository.findAllByBookPublisher(bookPublisher);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by publisher, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllNotExpired(LocalDateTime startDate) {
        try {
            return offerRepository.findAllByNotExpired(startDate);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects before expiration, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllCreatedBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        if(!startDate.isBefore(endDate))
            throw new GetException("Start date smaller than end date");
        try {
            return offerRepository.findAllByCreatedAtBetween(startDate, endDate);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects between dates, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllByOfferOwner(User user) {
        try {
            return offerRepository.findAllByOfferOwner(user);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by owner, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllByCity(String city) {
        try {
            return offerRepository.findAllByCity(city);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by city, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAllByVoivodeship(String voivodeship) {
        try {
            return offerRepository.findAllByVoivodeship(voivodeship);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by voivodeship, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getByFilter(Offer offer) {
        ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withMatcher("city", regex().ignoreCase())
                .withMatcher("voivodeship", regex().ignoreCase())
                .withMatcher("offerName", startsWith().ignoreCase())
                .withMatcher("bookTitle", regex().ignoreCase())
                .withMatcher("bookPublisher", regex().ignoreCase())
                .withMatcher("bookReleaseYear", startsWith());
        Example<Offer> offerExample = Example.of(offer, exampleMatcher);
        return offerRepository.findAll(offerExample);
    }
}
