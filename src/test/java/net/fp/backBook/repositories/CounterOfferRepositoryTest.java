package net.fp.backBook.repositories;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class CounterOfferRepositoryTest {

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
        this.counterOfferRepository.deleteAll();
        this.offerRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void testFindByUser() {
        User user = User.builder()
                .name("userName").build();
        user = this.userRepository.insert(user);
        CounterOffer counterOffer = CounterOffer.builder()
                .user(user).build();
        counterOffer = this.counterOfferRepository.insert(counterOffer);

        List<CounterOffer> result = this.counterOfferRepository.findByUser(user);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(counterOffer, result.get(0));
    }

    @Test
    public void testFindByOffer() {
        Offer offer = Offer.builder()
                .offerName("offerName").build();
        offer = this.offerRepository.insert(offer);
        CounterOffer counterOffer = CounterOffer.builder()
                .offer(offer).build();
        counterOffer = this.counterOfferRepository.insert(counterOffer);

        List<CounterOffer> result = this.counterOfferRepository.findByOffer(offer);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(counterOffer, result.get(0));
    }

    @Test
    public void testFindAllByNotExpired() {
        LocalDateTime startDate = LocalDateTime.of(2019, 1, 9, 13, 12);
        Offer offer = Offer.builder()
                .expires(startDate.plusDays(1)).build();

        this.offerRepository.insert(offer);
        Assert.assertEquals(
                1, this.offerRepository.findAllByNotExpired(startDate).size());
        Assert.assertEquals(
                0, this.offerRepository.findAllByNotExpired(startDate.plusDays(2)).size());
    }
}
