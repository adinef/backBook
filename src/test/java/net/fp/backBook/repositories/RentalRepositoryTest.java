package net.fp.backBook.repositories;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CounterOfferRepository counterOfferRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        this.clean();
    }

    @After
    public void tearDown() {
        this.clean();
    }

    private void clean() {
        this.rentalRepository.deleteAll();
        this.counterOfferRepository.deleteAll();
        this.offerRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void testFindByUser() {
        User user = User.builder()
                .name("userName").build();
        user = this.userRepository.insert(user);
        Rental rental = Rental.builder()
                .user(user).build();
        rental = this.rentalRepository.insert(rental);

        List<Rental> result = this.rentalRepository.findAllByUser(user);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(rental, result.get(0));
    }

    @Test
    public void testFindByOffer() {
        Offer offer = Offer.builder()
                .offerName("offerName").build();
        offer = this.offerRepository.insert(offer);
        Rental rental = Rental.builder()
                .offer(offer).build();
        rental = this.rentalRepository.insert(rental);

        Optional<Rental> result = this.rentalRepository.findByOffer(offer);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(rental, result.get());
    }

    @Test
    public void testFindByCounterOffer() {
        CounterOffer counterOffer = CounterOffer.builder().build();
        counterOffer = this.counterOfferRepository.insert(counterOffer);
        Rental rental = Rental.builder()
                .counterOffer(counterOffer).build();
        rental = this.rentalRepository.insert(rental);

        Optional<Rental> result = this.rentalRepository.findByCounterOffer(counterOffer);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(rental, result.get());
    }

    @Test
    public void testFindAllByNotExpired() {
        LocalDateTime startDate = LocalDateTime.of(2019, 1, 9, 13, 12);
        Rental rental = Rental.builder()
                .expires(startDate.plusDays(1)).build();

        this.rentalRepository.insert(rental);
        Assert.assertEquals(
                1, this.rentalRepository.findAllByNotExpired(startDate).size());
        Assert.assertEquals(
                0, this.rentalRepository.findAllByNotExpired(startDate.plusDays(2)).size());
    }
}
