package net.fp.backBook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import net.fp.backBook.configuration.TestMongoConfiguration;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {TestMongoConfiguration.class})
@ComponentScan(basePackageClasses = {OfferRepository.class, UserRepository.class})
public class OfferRepositoryTests {

    @Autowired
    private MongoClient client;
    @Autowired
    private MongoServer server;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        offerRepository.deleteAll();
    }

    @Test
    public void testFindByName() {
        offerRepository.insert( Offer.builder().offerName("test").build() );
        Offer offer = offerRepository.findByOfferName("test").get();
        assert offer != null;
        assert offer.getOfferName().equals("test");
    }

    @Test
    public void testFindByOfferOwner() {
        User testUser = User.builder().lastName("name").build();
        userRepository.insert(testUser);
        offerRepository.insert( Offer.builder().offerName("test").offerOwner(testUser).build() );
        Offer offer = offerRepository.findByOfferName("test").get();
        assert offer != null;
        assert offer.getOfferName().equals("test");
        assert offer.getOfferOwner().equals(testUser);
    }

    @Test
    public void testFindAllByExpiresBetween() {
        Offer testOffer = Offer.builder()
                .expires(LocalDateTime.now().plusDays(5))
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        LocalDateTime expiresStartTime = LocalDateTime.now().minusDays(5);
        LocalDateTime expiresTopTime = LocalDateTime.now();
        offerRepository.insert(testOffer);
        assert offerRepository.findAllByExpiresBetween(expiresStartTime, expiresTopTime).size() == 0;
        assert offerRepository.findAllByExpiresBetween(expiresStartTime, expiresTopTime.plusDays(7)).size() != 0;
    }

    @Test
    public void testFindAllByCreatedAtBetween() {
        Offer testOffer = Offer.builder()
                .expires(LocalDateTime.now().plusDays(5))
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        LocalDateTime createdStartTime = LocalDateTime.now().minusDays(3);
        LocalDateTime createdTopTime = LocalDateTime.now();
        offerRepository.insert(testOffer);
        assert offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime).size() != 0;
        assert offerRepository.findAllByCreatedAtBetween(createdStartTime.plusDays(3), createdTopTime).size() == 0;
    }

    @Test
    public void testFindAllByNotExpired() {
        Offer testOffer = Offer.builder()
                .expires(LocalDateTime.now().plusDays(5))
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        LocalDateTime topDate = LocalDateTime.now();
        offerRepository.insert(testOffer);
        assert offerRepository.findAllByNotExpired(topDate).size() != 0;
        assert offerRepository.findAllByNotExpired(topDate.plusDays(6)).size() == 0;
    }

    @Test
    public void testFindAllByBookTitle() {
        Offer testOffer = Offer.builder().bookTitle("title").build();
        Offer testOffer2 = Offer.builder().bookTitle("title").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByBookTitle("title");
        assert fetched.size() == 2;
    }

    @Test
    public void testFindAllByBookPublisher() {
        Offer testOffer = Offer.builder().bookPublisher("pub").build();
        Offer testOffer2 = Offer.builder().bookPublisher("pub").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByBookPublisher("pub");
        assert fetched.size() == 2;
    }

    @Test
    public void testFindAllByCity() {
        Offer testOffer = Offer.builder().city("city").build();
        Offer testOffer2 = Offer.builder().city("city").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByCity("city");
        assert fetched.size() == 2;
    }
    @Test
    public void testFindAllByVoivodeship() {
        Offer testOffer = Offer.builder().voivodeship("voi").build();
        Offer testOffer2 = Offer.builder().voivodeship("voi").build();
        offerRepository.insert(testOffer);
        offerRepository.insert(testOffer2);
        List<Offer> fetched = offerRepository.findAllByVoivodeship("voi");
        assert fetched.size() == 2;
    }
}
