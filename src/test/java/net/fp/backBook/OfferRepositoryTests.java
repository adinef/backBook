package net.fp.backBook;

import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import net.fp.backBook.repositories.UserRepository;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ComponentScan(basePackageClasses = {OfferRepository.class, UserRepository.class})
public class OfferRepositoryTests {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        offerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        offerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFindByName() {
        offerRepository.insert( Offer.builder().offerName("test").build() );
        Offer offer = offerRepository.findAllByOfferName("test").get(0);
        Assert.assertNotNull(offer);
        Assert.assertEquals(offer.getOfferName(), "test");
    }

    @Test
    public void testFindByOfferOwner() {
        User testUser = User.builder().lastName("name").build();
        userRepository.insert(testUser);
        offerRepository.insert( Offer.builder().offerName("test").offerOwner(testUser).build() );
        Offer offer = offerRepository.findAllByOfferName("test").get(0);
        Assert.assertNotNull(offer);
        Assert.assertEquals(offer.getOfferOwner(), testUser);
    }

    @Test
    public void testFindAllByExpiresBetween() {
        Offer testOffer = Offer.builder()
                .expires(LocalDateTime.now().plusDays(5))
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        LocalDateTime expiresStartTime = LocalDateTime.now().minusDays(5);
        LocalDateTime expiresTopTime = LocalDateTime.now();
        offerRepository.insert(testOffer);
        Assert.assertEquals(
                0, offerRepository.findAllByExpiresBetween(expiresStartTime, expiresTopTime).size());
        Assert.assertNotEquals(
                0, offerRepository.findAllByExpiresBetween(expiresStartTime, expiresTopTime.plusDays(7)).size());
    }

    @Test
    public void testFindAllByCreatedAtBetween() {
        Offer testOffer = Offer.builder()
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        LocalDateTime createdStartTime = LocalDateTime.now().minusDays(5);
        LocalDateTime createdTopTime = LocalDateTime.now();
        offerRepository.insert(testOffer);
        Assert.assertEquals(
                1, offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime).size());
        Assert.assertEquals(
                0, offerRepository.findAllByCreatedAtBetween(createdStartTime.plusDays(4), createdTopTime).size());
    }

    @Test
    public void testFindAllByNotExpired() {
        Offer testOffer = Offer.builder()
                .expires(LocalDateTime.now().plusDays(5))
                .build();
        LocalDateTime topDate = LocalDateTime.now();
        offerRepository.insert(testOffer);
        Assert.assertEquals(
                1, offerRepository.findAllByNotExpired(topDate).size());
        Assert.assertEquals(
                0, offerRepository.findAllByNotExpired(topDate.plusDays(6)).size());
    }

    @Test
    public void testFindAllByBookTitle() {
        Offer testOffer = Offer.builder().bookTitle("title").build();
        Offer testOffer2 = Offer.builder().bookTitle("title").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByBookTitle("title");
        Assert.assertEquals(2, fetched.size());
    }

    @Test
    public void testFindAllByBookPublisher() {
        Offer testOffer = Offer.builder().bookPublisher("pub").build();
        Offer testOffer2 = Offer.builder().bookPublisher("pub").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByBookPublisher("pub");
        Assert.assertEquals(2, fetched.size());
    }

    @Test
    public void testFindAllByCity() {
        Offer testOffer = Offer.builder().city("city").build();
        Offer testOffer2 = Offer.builder().city("city").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByCity("city");
        Assert.assertEquals(2, fetched.size());
    }
    @Test
    public void testFindAllByVoivodeship() {
        Offer testOffer = Offer.builder().voivodeship("voi").build();
        Offer testOffer2 = Offer.builder().voivodeship("voi").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByVoivodeship("voi");
        Assert.assertEquals(2, fetched.size());
    }
}
