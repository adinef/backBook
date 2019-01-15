package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.*;
import net.fp.backBook.model.Rental;
import net.fp.backBook.repositories.CounterOfferRepository;
import net.fp.backBook.repositories.RentalRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class RentalServiceTest {
    
    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    public void addTestSuccess() {
        Rental rental = mock(Rental.class);

        when(this.rentalRepository.insert(rental)).thenReturn(rental);

        Assert.assertEquals(rental, this.rentalService.add(rental));
        verify(this.rentalRepository, times(1)).insert(rental);
    }

    @Test(expected = AddException.class)
    public void addTestFailure() {
        Rental rental = mock(Rental.class);

        when(this.rentalRepository.insert(rental)).thenThrow(RuntimeException.class);

        this.rentalService.add(rental);
    }

    @Test
    public void modifyTestSuccess() {
        Rental rental = Rental.builder().id("1").build();

        when(this.rentalRepository.save(rental)).thenReturn(rental);

        Assert.assertEquals(rental, this.rentalService.modify(rental));
        verify(this.rentalRepository, times(1)).save(rental);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureIdNull() {
        Rental rental = Rental.builder().id(null).build();

        this.rentalService.modify(rental);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureDatabaseException() {
        Rental rental = Rental.builder().id("1").build();

        when(this.rentalRepository.save(any(Rental.class))).thenThrow(RuntimeException.class);

        this.rentalService.modify(rental);
    }

    @Test
    public void getAllTestSuccess() {
        List<Rental> rentals = Arrays.asList(mock(Rental.class), mock(Rental.class));

        when(this.rentalRepository.findAll()).thenReturn(rentals);

        Assert.assertEquals(rentals, this.rentalService.getAll());
        verify(this.rentalRepository, times(1)).findAll();
    }

    @Test(expected = GetException.class)
    public void getAllTestFailure() {
        when(this.rentalRepository.findAll()).thenThrow(RuntimeException.class);

        this.rentalService.getAll();
    }

    @Test
    public void getByIdTestSuccess() {
        Rental rental = mock(Rental.class);

        when(this.rentalRepository.findById(anyString())).thenReturn(Optional.of(rental));

        Assert.assertEquals(rental, this.rentalService.getById(anyString()));
        verify(this.rentalRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureNotPresent() {
        when(this.rentalRepository.findById(anyString())).thenReturn(Optional.empty());

        this.rentalService.getById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureDatabaseException() {
        when(this.rentalRepository.findById(anyString())).thenThrow(RuntimeException.class);

        this.rentalService.getById(anyString());
    }

    @Test
    public void deleteTestSuccess() {
        Rental rental = Rental.builder().id("1").build();

        doNothing().when(this.rentalRepository).deleteById(rental.getId());

        this.rentalService.delete(rental.getId());
        verify(this.rentalRepository, times(1)).deleteById(rental.getId());
    }

    @Test(expected = DeleteException.class)
    public void deleteTestFailure() {
        Rental rental = Rental.builder().id("1").build();

        doThrow(RuntimeException.class).when(this.rentalRepository).deleteById(rental.getId());

        this.rentalService.delete(rental.getId());
    }

    @Test
    public void getAllByOfferTestSuccess() {
        Rental rental = mock(Rental.class);
        Offer offer = mock(Offer.class);

        when(this.rentalRepository.findByOffer(offer)).thenReturn(Optional.of(rental));

        Assert.assertEquals(rental, this.rentalService.getByOffer(offer));
        verify(this.rentalRepository, times(1)).findByOffer(offer);
    }

    @Test(expected = GetException.class)
    public void getAllByOfferTestFailure() {
        Offer offer = mock(Offer.class);

        when(this.rentalRepository.findByOffer(offer)).thenThrow(RuntimeException.class);

        this.rentalService.getByOffer(offer);
    }

    @Test
    public void getAllByUserTestSuccess() {
        List<Rental> rentals = Arrays.asList(mock(Rental.class), mock(Rental.class));
        User user = mock(User.class);

        when(this.rentalRepository.findAllByUser(user)).thenReturn(rentals);

        Assert.assertEquals(rentals, this.rentalService.getAllByUser(user));
        verify(this.rentalRepository, times(1)).findAllByUser(user);
    }

    @Test(expected = GetException.class)
    public void getAllByUserTestFailure() {
        User user = mock(User.class);

        when(this.rentalRepository.findAllByUser(user)).thenThrow(RuntimeException.class);

        this.rentalService.getAllByUser(user);
    }

    @Test
    public void getAllByCounterOfferTestSuccess() {
        Rental rental = mock(Rental.class);
        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.rentalRepository.findByCounterOffer(counterOffer)).thenReturn(Optional.of(rental));

        Assert.assertEquals(rental, this.rentalService.getByCounterOffer(counterOffer));
        verify(this.rentalRepository, times(1)).findByCounterOffer(counterOffer);
    }

    @Test(expected = GetException.class)
    public void getAllByCounterTestFailure() {
        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.rentalRepository.findByCounterOffer(counterOffer)).thenThrow(RuntimeException.class);

        this.rentalService.getByCounterOffer(counterOffer);
    }

    @Test
    public void getAllByNotExpiredTestSuccess() {
        List<Rental> rentals = Arrays.asList(mock(Rental.class), mock(Rental.class));
        LocalDateTime localDateTime = LocalDateTime.now();

        when(this.rentalRepository.findAllByNotExpired(localDateTime)).thenReturn(rentals);

        Assert.assertEquals(rentals, this.rentalService.getAllByNotExpired(localDateTime));
        verify(this.rentalRepository, times(1)).findAllByNotExpired(localDateTime);
    }

    @Test(expected = GetException.class)
    public void getAllByNotExpiredFailure() {
        LocalDateTime localDateTime = LocalDateTime.now();

        when(this.rentalRepository.findAllByNotExpired(localDateTime)).thenThrow(RuntimeException.class);

        this.rentalService.getAllByNotExpired(localDateTime);
    }
}
