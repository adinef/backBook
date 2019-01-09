package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.repositories.CounterOfferRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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
}
