package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.RentalDto;
import net.fp.backBook.exceptions.ModifyException;
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
    public RentalDto getById(@PathVariable("id") String id) {
        Rental rental = this.rentalService.getById(id);
        return this.modelMapper.map(rental, RentalDto.class);
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = this.rentalService.getAllRentals();
        List<RentalDto> rentalDtos = new LinkedList<>();
        rentals.forEach(rental -> rentalDtos.add(this.modelMapper.map(rental, RentalDto.class)));
        return rentalDtos;
    }

    @DeleteMapping(
            value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRental(@PathVariable("id") String id) {
        this.rentalService.deleteRental(id);
    }
}
