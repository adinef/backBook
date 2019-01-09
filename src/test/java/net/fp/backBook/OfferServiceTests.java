package net.fp.backBook;

import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import net.fp.backBook.repositories.UserRepository;
import net.fp.backBook.services.OfferService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services"})
@SpringBootTest
public class OfferServiceTests {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfferService offerService;

    private ArrayList<Offer> offersList;
    private User globalTestUser;

    @Before
    public void setUp() {
        userRepository.deleteAll();

        User testUser = User.builder().name("name").lastName("lastName").build();
        User testUser2 = User.builder().name("name1").lastName("lastName1").build();
        User testUser3 = User.builder().name("name2").lastName("lastName2").build();
        globalTestUser = testUser;
        userRepository.insert(testUser);
        userRepository.insert(testUser2);
        userRepository.insert(testUser3);

        offerRepository.deleteAll();
        Offer test1 = Offer
                .builder()
                .offerName("test1")
                .createdAt(LocalDateTime.now().minusDays(3))
                .expires(LocalDateTime.now().plusDays(3))
                .offerOwner(testUser)
                .city("Lodz")
                .voivodeship("lodzkie")
                .bookPublisher("pub")
                .bookTitle("book1")
                .build();
        Offer test2 = Offer
                .builder()
                .offerName("test2")
                .createdAt(LocalDateTime.now().minusDays(1))
                .expires(LocalDateTime.now().plusDays(6))
                .offerOwner(testUser2)
                .city("Warszawa")
                .voivodeship("mazowieckie")
                .bookPublisher("pub")
                .bookTitle("book2")
                .build();
        Offer test3 = Offer
                .builder()
                .offerName("test3")
                .createdAt(LocalDateTime.now().minusDays(4))
                .expires(LocalDateTime.now().plusDays(1))
                .offerOwner(testUser)
                .city("Andrespol")
                .voivodeship("lodzkie")
                .bookPublisher("pub")
                .bookTitle("book1")
                .build();
        offersList = new ArrayList<>();
        offersList.add(test1);
        offersList.add(test2);
        offersList.add(test3);
        offerRepository.insert(offersList);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        offerRepository.deleteAll();
    }

    @Test
    public void testGetById() {
        Offer offer = offersList.get(0);
        Offer offerFetched = offerService.getById(offer.getId());
        Assert.assertNotNull(offerFetched);
        Assert.assertEquals(offer.getOfferName(), offerFetched.getOfferName());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrows() {
        offerService.getById("-1");
    }

    @Test
    public void testGetAllOffers() {
        int offersSize = offersList.size();
        int fetechedOffersSize = offerService.getAllOffers().size();
        Assert.assertNotNull(fetechedOffersSize);
        Assert.assertEquals(offersSize, fetechedOffersSize);
    }

    @Test
    public void testGetAllByBookTitle() {
        long offersByBookTitleSize = offersList
                .stream()
                .filter(n -> n.getBookTitle()
                        .equals("book1")).count();
        long offersFetchedByBookTitleSize = offerService.getAllByBookTitle("book1").size();
        Assert.assertEquals(offersByBookTitleSize, offersFetchedByBookTitleSize);
    }

    @Test
    public void testGetAllNotExpired() {
        LocalDateTime topDate = LocalDateTime.now();
        Assert.assertEquals(
                0, offerRepository.findAllByNotExpired(topDate).size());
        Assert.assertNotEquals(
               0, offerRepository.findAllByNotExpired(topDate.plusDays(6)).size());
    }

    @Test
    public void testGetAllCreatedBetween() {
        LocalDateTime createdStartTime = LocalDateTime.now().minusDays(3);
        LocalDateTime createdTopTime = LocalDateTime.now();
        Assert.assertEquals(
                0, offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime).size());
        Assert.assertNotEquals(
                0, offerRepository.findAllByCreatedAtBetween(createdStartTime.plusDays(3), createdTopTime).size());
    }

    @Test(expected = GetException.class)
    public void testGetAllCreatedBetweenThrowsOnStartBiggerThanEnd() {
        LocalDateTime createdStartTime = LocalDateTime.now();
        LocalDateTime createdTopTime = LocalDateTime.now().minusDays(3);
        offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime);
    }

    @Test
    public void testGetAllByOfferOwner() {
        long offersByOwnerSize =
                offersList
                .stream()
                .filter( n -> n.getOfferOwner().equals(globalTestUser))
                .count();
        long offersFetchedByOwnerSize =
                offerService
                .getAllByOfferOwner(globalTestUser)
                .size();
        Assert.assertEquals(offersByOwnerSize, offersFetchedByOwnerSize);
    }
/*
    @Test(expected = GetException.class)
    public void testGetAllByOfferOwnerThrowsOnNull() {
        offerService.getAllByOfferOwner(null);
    }
*/
    @Test(expected = GetException.class)
    public void testGetAllByOfferOwnerThrowsOnNonConnected() {
        offerService.getAllByOfferOwner(User.builder().name("non-existing").build());
    }

    @Test
    public void testGetAllByCity() {
        long offersByCitySize =
                offersList
                .stream()
                .filter( n -> n.getCity().equals("Lodz"))
                .count();
        long offersFetchedByCitySize =
                offerService
                .getAllByCity("Lodz")
                .size();
        Assert.assertEquals(offersByCitySize, offersFetchedByCitySize);
    }

    @Test
    public void testGetAllByVoivodeship() {
        long offersByVoivodeshipSize =
                offersList
                        .stream()
                        .filter( n -> n.getVoivodeship().equals("lodzkie"))
                        .count();
        long offersFetchedByVoivodeshipSize =
                offerService
                        .getAllByVoivodeship("lodzkie")
                        .size();
        Assert.assertEquals(offersByVoivodeshipSize, offersFetchedByVoivodeshipSize);
    }

    @Test
    public void testGetByFilter() {
        long offersByFilter =
                offersList
                        .stream()
                        .filter( n -> n.getVoivodeship().equals("lodzkie") && n.getCity().equals("Lodz"))
                        .count();
        Offer filterOffer = Offer.builder().city("Lodz").voivodeship("lodzkie").build();
        long offersFetchedByFilter =
                offerService
                        .getByFilter(filterOffer)
                        .size();
        Assert.assertEquals(offersByFilter, offersFetchedByFilter);
    }

}
