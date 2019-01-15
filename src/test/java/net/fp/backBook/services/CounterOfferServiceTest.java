package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.CounterOfferRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class CounterOfferServiceTest {

    @Mock
    private CounterOfferRepository counterOfferRepository;

    @InjectMocks
    private CounterOfferServiceImpl counterOfferService;

    @Test
    public void addTestSuccess() {
        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.counterOfferRepository.insert(counterOffer)).thenReturn(counterOffer);

        Assert.assertEquals(counterOffer, this.counterOfferService.add(counterOffer));
        verify(this.counterOfferRepository, times(1)).insert(counterOffer);
    }

    @Test(expected = AddException.class)
    public void addTestFailure() {
        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.counterOfferRepository.insert(counterOffer)).thenThrow(RuntimeException.class);

        this.counterOfferService.add(counterOffer);
    }

    @Test
    public void modifyTestSuccess() {
        CounterOffer counterOffer = CounterOffer.builder().id("1").build();

        when(this.counterOfferRepository.save(counterOffer)).thenReturn(counterOffer);

        Assert.assertEquals(counterOffer, this.counterOfferService.modify(counterOffer));
        verify(this.counterOfferRepository, times(1)).save(counterOffer);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureIdNull() {
        CounterOffer counterOffer = CounterOffer.builder().id(null).build();

        this.counterOfferService.modify(counterOffer);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureDatabaseException() {
        CounterOffer counterOffer = CounterOffer.builder().id("1").build();

        when(this.counterOfferRepository.save(any(CounterOffer.class))).thenThrow(RuntimeException.class);

        this.counterOfferService.modify(counterOffer);
    }

    @Test
    public void getAllTestSuccess() {
        List<CounterOffer> counterOffers = Arrays.asList(mock(CounterOffer.class), mock(CounterOffer.class));

        when(this.counterOfferRepository.findAll()).thenReturn(counterOffers);

        Assert.assertEquals(counterOffers, this.counterOfferService.getAll());
        verify(this.counterOfferRepository, times(1)).findAll();
    }

    @Test(expected = GetException.class)
    public void getAllTestFailure() {
        when(this.counterOfferRepository.findAll()).thenThrow(RuntimeException.class);

        this.counterOfferService.getAll();
    }

    @Test
    public void getByIdTestSuccess() {
        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.counterOfferRepository.findById(anyString())).thenReturn(Optional.of(counterOffer));

        Assert.assertEquals(counterOffer, this.counterOfferService.getById(anyString()));
        verify(this.counterOfferRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureNotPresent() {
        when(this.counterOfferRepository.findById(anyString())).thenReturn(Optional.empty());

        this.counterOfferService.getById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureDatabaseException() {
        when(this.counterOfferRepository.findById(anyString())).thenThrow(RuntimeException.class);

        this.counterOfferService.getById(anyString());
    }

    @Test
    public void deleteTestSuccess() {
        CounterOffer counterOffer = CounterOffer.builder().id("1").build();

        doNothing().when(this.counterOfferRepository).deleteById(counterOffer.getId());

        this.counterOfferService.delete(counterOffer.getId());
        verify(this.counterOfferRepository, times(1)).deleteById(counterOffer.getId());
    }

    @Test(expected = DeleteException.class)
    public void deleteTestFailure() {
        CounterOffer counterOffer = CounterOffer.builder().id("1").build();

        doThrow(RuntimeException.class).when(this.counterOfferRepository).deleteById(counterOffer.getId());

        this.counterOfferService.delete(counterOffer.getId());
    }

    @Test
    public void getAllByOfferTestSuccess() {
        List<CounterOffer> counterOffers = Arrays.asList(mock(CounterOffer.class), mock(CounterOffer.class));
        Offer offer = mock(Offer.class);

        when(this.counterOfferRepository.findByOffer(offer)).thenReturn(counterOffers);

        Assert.assertEquals(counterOffers, this.counterOfferService.getAllByOffer(offer));
        verify(this.counterOfferRepository, times(1)).findByOffer(offer);
    }

    @Test(expected = GetException.class)
    public void getAllByOfferTestFailure() {
        Offer offer = mock(Offer.class);

        when(this.counterOfferRepository.findByOffer(offer)).thenThrow(RuntimeException.class);

        this.counterOfferService.getAllByOffer(offer);
    }

    @Test
    public void getAllByUserTestSuccess() {
        List<CounterOffer> counterOffers = Arrays.asList(mock(CounterOffer.class), mock(CounterOffer.class));
        User user = mock(User.class);

        when(this.counterOfferRepository.findByUser(user)).thenReturn(counterOffers);

        Assert.assertEquals(counterOffers, this.counterOfferService.getAllByUser(user));
        verify(this.counterOfferRepository, times(1)).findByUser(user);
    }

    @Test(expected = GetException.class)
    public void getAllByUserTestFailure() {
        User user = mock(User.class);

        when(this.counterOfferRepository.findByUser(user)).thenThrow(RuntimeException.class);

        this.counterOfferService.getAllByUser(user);
    }

    @Test
    public void getAllByNotExpiredTestSuccess() {
        List<CounterOffer> counterOffers = Arrays.asList(mock(CounterOffer.class), mock(CounterOffer.class));
        LocalDateTime localDateTime = LocalDateTime.now();

        when(this.counterOfferRepository.findAllByNotExpired(localDateTime)).thenReturn(counterOffers);

        Assert.assertEquals(counterOffers, this.counterOfferService.getAllByNotExpired(localDateTime));
        verify(this.counterOfferRepository, times(1)).findAllByNotExpired(localDateTime);
    }

    @Test(expected = GetException.class)
    public void getAllByNotExpiredFailure() {
        LocalDateTime localDateTime = LocalDateTime.now();

        when(this.counterOfferRepository.findAllByNotExpired(localDateTime)).thenThrow(RuntimeException.class);

        this.counterOfferService.getAllByNotExpired(localDateTime);
    }
}
