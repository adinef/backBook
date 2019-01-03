package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.CounterOfferDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.CounterOfferService;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/counterOffers")
public class CounterOfferController {

    private CounterOfferService counterOfferService;

    private OfferService offerService;

    private UserService userService;

    private ModelMapper modelMapper;

    @Autowired
    public CounterOfferController(CounterOfferService counterOfferService, OfferService offerService,
                                  UserService userService, ModelMapper modelMapper) {
        this.counterOfferService = counterOfferService;
        this.offerService = offerService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CounterOfferDto addCounterOffer(@RequestBody CounterOfferDto counterOfferDto) {
        CounterOffer counterOffer = this.modelMapper.map(counterOfferDto, CounterOffer.class);
        counterOffer = this.counterOfferService.addCounterOffer(counterOffer);
        return this.modelMapper.map(counterOffer, CounterOfferDto.class);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CounterOfferDto modifyCounterOffer(@PathVariable("id") String id, @RequestBody CounterOfferDto counterOfferDto) {
        if (!counterOfferDto.getId().equals(id)) {
            throw new ModifyException("Ids are not the same");
        } else {
            CounterOffer counterOffer = this.modelMapper.map(counterOfferDto, CounterOffer.class);
            counterOffer = this.counterOfferService.modifyCounterOffer(counterOffer);
            return this.modelMapper.map(counterOffer, CounterOfferDto.class);
        }
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CounterOfferDto getCounterOfferById(@PathVariable("id") String id) {
        CounterOffer counterOffer = this.counterOfferService.getById(id);
        return this.modelMapper.map(counterOffer, CounterOfferDto.class);
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CounterOfferDto> getAllCounterOffers() {
        List<CounterOffer> counterOffers = this.counterOfferService.getAllCounterOffers();
        return this.getDtosList(counterOffers);
    }

    @DeleteMapping(
            value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCounterOffer(@PathVariable("id") String id) {
        this.counterOfferService.deleteCounterOffer(id);
    }

    @GetMapping(
            value = "/offer/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CounterOfferDto> getAllCounterOffersByOffer(@PathVariable("id") String id) {
        Offer offer = this.offerService.getById(id);
        List<CounterOffer> counterOffers = this.counterOfferService.getAllByOffer(offer);
        return this.getDtosList(counterOffers);
    }

    @GetMapping(
            value = "/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CounterOfferDto> getAllCounterOffersByUser(@PathVariable("id") String id) {
        User user = this.userService.getUserById(id);
        List<CounterOffer> counterOffers = this.counterOfferService.getAllByUser(user);
        return this.getDtosList(counterOffers);
    }

    @GetMapping(
            value = "/notExpired/{dateString}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CounterOfferDto> getAllCounterOffersByNotExpired(@PathVariable String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<CounterOffer> counterOffers = this.counterOfferService.getAllByNotExpired(date);
        return getDtosList(counterOffers);
    }

    private List<CounterOfferDto> getDtosList(List<CounterOffer> counterOffers) {
        List<CounterOfferDto> counterOfferDtos = new LinkedList<>();
        counterOffers.forEach(counterOffer -> counterOfferDtos.add(this.modelMapper.map(counterOffer, CounterOfferDto.class)));
        return counterOfferDtos;
    }
}
