package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.RentalDto;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.Rental;
import net.fp.backBook.services.RentalService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {

    private RentalService rentalService;

    private ModelMapper modelMapper;

    @Autowired
    public RentalController(RentalService rentalService, ModelMapper modelMapper) {
        this.rentalService = rentalService;
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

    @GetMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = this.rentalService.getAllRentals();
        if (rentals.isEmpty()) {
            throw new GetException("Cannot find rentals.");
        } else {
            List<RentalDto> rentalDtos = new LinkedList<>();
            rentals.forEach(rental -> rentalDtos.add(this.modelMapper.map(rental, RentalDto.class)));
            return rentalDtos;
        }
    }
}
