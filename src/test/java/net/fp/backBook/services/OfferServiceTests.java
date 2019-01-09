package net.fp.backBook.services;

import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import net.fp.backBook.repositories.UserRepository;
import net.fp.backBook.services.OfferService;
import org.apache.tomcat.jni.Local;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services"})
@SpringBootTest
public class OfferServiceTests {

    /*
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferService offerService;

    @Autowired
    private UserRepository userRepository;

*/
    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    public void testGetById() {
        Offer offer = mock(Offer.class);
        when(this.offerRepository.findById(anyString())).thenReturn(Optional.of(offer));
        Offer offerFetched = offerService.getById(anyString());
        Assert.assertNotNull(offerFetched);
        Assert.assertEquals(offer.getOfferName(), offerFetched.getOfferName());
        verify(this.offerRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrowsOnNotPresent() {
        when(this.offerRepository.findById(anyString())).thenReturn(Optional.empty());
        this.offerService.getById(anyString());
    }

    @Test
    public void testGetAllOffers() {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(this.offerRepository.findAll()).thenReturn(offers);
        Assert.assertEquals(offers, this.offerService.getAll());
    }

    @Test(expected = GetException.class)
    public void testGetAllOffersThrowsOnRuntimeException() {
        when(this.offerRepository.findAll()).thenThrow(RuntimeException.class);
        this.offerService.getAll();
    }

    @Test
    public void testGetAllByBookTitle() {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        String title = anyString();
        when(this.offerRepository.findAllByBookTitle(title)).thenReturn(offers);
        Assert.assertEquals(offers, this.offerService.getAllByBookTitle(title));
        verify(this.offerRepository, times(1)).findAllByBookTitle(title);
    }

    @Test(expected = GetException.class)
    public void testGetAllByBookTitleThrowsOnRuntimeException() {
        when(this.offerRepository.findAllByBookTitle(anyString())).thenThrow(RuntimeException.class);
        this.offerService.getAllByBookTitle(anyString());
    }

    @Test
    public void testGetAllNotExpired() {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        LocalDateTime topDate = LocalDateTime.now().plusDays(4);
        when(this.offerRepository.findAllByNotExpired(topDate)).thenReturn(offers);
        Assert.assertEquals(offers, offerService.getAllNotExpired(topDate));
    }

    @Test(expected = GetException.class)
    public void testGetAllNotExpiredThrowsOnRuntimeException() {
        LocalDateTime ldt = LocalDateTime.now();
        when(this.offerRepository.findAllByNotExpired(ldt)).thenThrow(RuntimeException.class);
        offerService.getAllNotExpired(ldt);
    }


    @Test
    public void testGetAllCreatedBetween() {
        LocalDateTime createdStartTime = LocalDateTime.now().minusDays(5);
        LocalDateTime createdTopTime = LocalDateTime.now().minusDays(3);
        LocalDateTime createdTopTime2 = LocalDateTime.now().minusDays(1);
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(this.offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime))
                .thenReturn(offers);
        when(this.offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime2))
                .thenReturn(new ArrayList<>());

        Assert.assertEquals(offers,
                this.offerService.getAllCreatedBetweenDates(createdStartTime, createdTopTime));
        Assert.assertEquals(new ArrayList<>(),
                this.offerService.getAllCreatedBetweenDates(createdStartTime, createdTopTime2));
    }


    @Test(expected = GetException.class)
    public void testGetAllCreatedBetweenThrowsOnStartBiggerThanEnd() {
        LocalDateTime createdStartTime = LocalDateTime.now();
        LocalDateTime createdTopTime = LocalDateTime.now().minusDays(3);
        when(this.offerRepository.findAllByCreatedAtBetween(createdStartTime, createdTopTime))
                .thenThrow(GetException.class);
        offerService.getAllCreatedBetweenDates(createdStartTime, createdTopTime);
    }

    @Test
    public void testGetAllByOfferOwner() {
        User user = mock(User.class);
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(this.offerRepository.findAllByOfferOwner(user)).thenReturn(offers);
        Assert.assertEquals(offers, this.offerService.getAllByOfferOwner(user));
        verify(this.offerRepository, times(1)).findAllByOfferOwner(user);
    }

    @Test(expected = GetException.class)
    public void testGetAllByOfferOwnerThrowsOnNonConnected() {
        User user = mock(User.class);
        when(this.offerRepository.findAllByOfferOwner(user)).thenThrow(RuntimeException.class);
        offerService.getAllByOfferOwner(user);
    }

    @Test
    public void testGetAllByCity() {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(this.offerRepository.findAllByCity(anyString())).thenReturn(offers);
        Assert.assertEquals(offers, this.offerService.getAllByCity(anyString()));
        verify(this.offerRepository, times(1)).findAllByCity(anyString());
    }

    @Test
    public void testGetAllByVoivodeship() {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(this.offerRepository.findAllByVoivodeship(anyString())).thenReturn(offers);
        Assert.assertEquals(offers, this.offerService.getAllByVoivodeship(anyString()));
        verify(this.offerRepository, times(1)).findAllByVoivodeship(anyString());
    }


    private Example<Offer> testFilter(Offer offer) {
        return Example.of(offer);
    }

    @Test
    public void testGetByFilter() {
        Offer filterOffer = mock(Offer.class);
        when(this.offerRepository.findAll(testFilter(filterOffer))).thenReturn(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), this.offerService.getByFilter(filterOffer));
        verify(this.offerRepository, times(1)).findAll(testFilter(filterOffer));
    }
/*
    @Test
    public void testAddOffer() {
        Offer newOffer = Offer.builder().offerName("newOffer").build();
        newOffer = offerService.add(newOffer);
        Assert.assertNotNull(newOffer.getId());
    }

    @Test(expected = GetException.class)
    public void testDeleteOffer() {
        Offer newOffer = Offer.builder().offerName("newOffer").build();
        newOffer = offerService.add(newOffer);
        Assert.assertNotNull(newOffer.getId());
        offerService.delete(newOffer.getId());
        offerService.getById(newOffer.getId());
    }

    @Test
    public void testModifyOffer() {
        Offer fetchedOffer = offerService.getAll().get(0);
        fetchedOffer.setCity("NEW NAME");
        offerService.modify(fetchedOffer);
        Offer fetchedAgainOffer = offerService.getById(fetchedOffer.getId());
        Assert.assertEquals(fetchedOffer.getCity(), fetchedAgainOffer.getCity());
    }

    @Test(expected = ModifyException.class)
    public void testModifyOfferThrowsOnDetached() {
        Offer newOffer = Offer.builder().build();
        offerService.modify(newOffer);
    }

*/
}
