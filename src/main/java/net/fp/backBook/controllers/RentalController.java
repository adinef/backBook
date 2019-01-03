package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.RentalDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Rental;
import net.fp.backBook.model.User;
import net.fp.backBook.services.CounterOfferService;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.RentalService;
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
@RequestMapping(value = "/rentals")
public class RentalController {

    private RentalService rentalService;

    private OfferService offerService;

    private UserService userService;

    private CounterOfferService counterOfferService;

    private ModelMapper modelMapper;

    @Autowired
    public RentalController(RentalService rentalService, OfferService offerService, UserService userService,
                            CounterOfferService counterOfferService, ModelMapper modelMapper) {
        this.rentalService = rentalService;
        this.offerService = offerService;
        this.userService = userService;
        this.counterOfferService = counterOfferService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDto addRental(@RequestBody RentalDto rentalDto) {
        Rental rental = this.modelMapper.map(rentalDto, Rental.class);
        rental = this.rentalService.addRental(rental);
        return this.modelMapper.map(rental, RentalDto.class);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RentalDto modifyRental(@PathVariable("id") String id, @RequestBody RentalDto rentalDto) {
        if (!rentalDto.getId().equals(id)) {
            throw new ModifyException("Ids are not the same");
        } else {
            Rental rental = this.modelMapper.map(rentalDto, Rental.class);
            rental = this.rentalService.modifyRental(rental);
            return this.modelMapper.map(rental, RentalDto.class);
        }
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RentalDto getRentalById(@PathVariable("id") String id) {
        Rental rental = this.rentalService.getById(id);
        return this.modelMapper.map(rental, RentalDto.class);
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = this.rentalService.getAllRentals();
        return this.getDtosList(rentals);
    }

    @DeleteMapping(
            value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRental(@PathVariable("id") String id) {
        this.rentalService.deleteRental(id);
    }

    @GetMapping(
            value = "/offer/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RentalDto getRentalByOffer(@PathVariable("id") String id) {
        Offer offer = this.offerService.getById(id);
        Rental rental = this.rentalService.getByOffer(offer);
        return this.modelMapper.map(rental, RentalDto.class);
    }

    @GetMapping(
            value = "/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RentalDto> getAllRentalsByUser(@PathVariable("id") String id) {
        User user = this.userService.getUserById(id);
        List<Rental> rentals = this.rentalService.getAllByUser(user);
        return this.getDtosList(rentals);
    }

    @GetMapping(
            value = "/counterOffer/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RentalDto getRentalByCounterOffer(@PathVariable("id") String id) {
        CounterOffer counterOffer = this.counterOfferService.getById(id);
        Rental rental = this.rentalService.getByCounterOffer(counterOffer);
        return this.modelMapper.map(rental, RentalDto.class);
    }

    @GetMapping(
            value = "/notExpired/{dateString}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RentalDto> getAllRentalsByNotExpired(@PathVariable String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Rental> rentals = this.rentalService.getAllByNotExpired(date);
        return getDtosList(rentals);
    }

    private List<RentalDto> getDtosList(List<Rental> rentals) {
        List<RentalDto> rentalDtos = new LinkedList<>();
        rentals.forEach(rental -> rentalDtos.add(this.modelMapper.map(rental, RentalDto.class)));
        return rentalDtos;
    }
}
