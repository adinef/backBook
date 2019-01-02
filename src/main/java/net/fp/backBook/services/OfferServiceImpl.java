package net.fp.backBook.services;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class OfferServiceImpl implements OfferService {

    private OfferRepository offerRepository;

    @Autowired
    public OfferServiceImpl(final OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public Offer getById(String id) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findById(id).orElseThrow( () -> new Exception("Cannot find offer by id.") );
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllOffers() {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAll();
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteOffer(String id) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            offerRepository.deleteById(id);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
    }

    @Override
    public Offer addOffer(Offer offer) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            offerRepository.insert(offer);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
        return offer;
    }

    @Override
    public Offer modifyOffer(Offer offer) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            offerRepository.save(offer);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
        return offer;
    }

    @Override
    public List<Offer> getAllByBookTitle(String bookTitle) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByBookTitle(bookTitle);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllByBookPublisher(String bookPublisher) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByBookPublisher(bookPublisher);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByCreatedAtBetween(startDate, endDate);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllByOfferOwner(User user) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByOfferOwner(user);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllByCity(String city) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByCity(city);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Offer> getAllByVoivodeship(String voivodeship) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return offerRepository.findAllByVoivodeship(voivodeship);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }
}
