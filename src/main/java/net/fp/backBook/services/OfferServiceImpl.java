package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.model.filters.OfferFilter;
import net.fp.backBook.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OfferServiceImpl implements OfferService {

    private OfferRepository offerRepository;
    private OfferFilter filter;

    @Autowired
    public OfferServiceImpl(
            final OfferRepository offerRepository,
            final OfferFilter filter
    ) {
        this.offerRepository = offerRepository;
        this.filter = filter;
    }

    @Override
    public Offer getById(String id) {
        try {
            return offerRepository.findById(id).orElseThrow( () -> new GetException("Cannot find offer by id."));
        } catch (final Exception e) {
            log.error("Error during getting Offer object, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getAll() {
        try {
            return offerRepository.findAll();
        } catch (final Exception e) {
            log.error("Error during getting Offer objects, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            offerRepository.deleteById(id);
        } catch (final Exception e) {
            log.error("Error during deleting Offer object by id, {}", e);
            throw new DeleteException(e.getMessage());
        }
    }

    @Override
    public Offer add(Offer offer) {
        try {
            offerRepository.insert(offer);
        } catch (final Exception e) {
            log.error("Error during inserting Offer object, {}", e);
            throw new AddException(e.getMessage());
        }
        return offer;
    }

    @Override
    public Offer modify(Offer offer) {
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
    public Page<Offer> getAllOffersByPage(int page, int limit) {
        try {
            return offerRepository.findAll(PageRequest.of(page, limit));
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by page, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<Offer> getByFilter(Offer offer) {
        try {
            Example<Offer> offerExample = Example.of(offer, filter.getMatcher());
            return offerRepository.findAll(offerExample);
        }catch (final Exception e) {
            log.error("Error during getting Offer objects by filter, {}", e);
            throw new GetException(e.getMessage());
        }
    }
    @Override
    public Page<Offer> getPageByFilter(Offer offer, int page, int limit) {
        try {
            Example<Offer> offerExample = Example.of(offer, filter.getMatcher());
            return offerRepository.findAll(offerExample, PageRequest.of(page, limit));
        } catch (final Exception e) {
            log.error("Error during getting users Offer objects by page (filtered), {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public boolean existsByIdAndOfferOwner(String id, User user) {
        try {
            return this.offerRepository.existsByIdAndOfferOwner(id, user);
        }catch (final Exception e) {
            log.error("Error during checking if user is offer owner, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public Page<Offer> getAllUsersOffersByPage(User user, int page, int limit) {
        try {
            return offerRepository.findAllByOfferOwner(user, PageRequest.of(page, limit));
        } catch (final Exception e) {
            log.error("Error during getting users Offer objects by page, {}", e);
            throw new GetException(e.getMessage());
        }
    }
}
