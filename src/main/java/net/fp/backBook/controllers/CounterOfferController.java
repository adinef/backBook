package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.CounterOfferDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.services.CounterOfferService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/counterOffers")
public class CounterOfferController {

    private CounterOfferService counterOfferService;

    private ModelMapper modelMapper;

    @Autowired
    public CounterOfferController(CounterOfferService counterOfferService, ModelMapper modelMapper) {
        this.counterOfferService = counterOfferService;
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
        List<CounterOfferDto> counterOfferDtos = new LinkedList<>();
        counterOffers.forEach(counterOffer -> counterOfferDtos.add(this.modelMapper.map(counterOffer, CounterOfferDto.class)));
        return counterOfferDtos;
    }

    @DeleteMapping(
            value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCounterOffer(@PathVariable("id") String id) {
        this.counterOfferService.deleteCounterOffer(id);
    }
}
