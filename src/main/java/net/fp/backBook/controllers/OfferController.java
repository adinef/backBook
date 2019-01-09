package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.DatePairDto;
import net.fp.backBook.dtos.OfferDto;
import net.fp.backBook.dtos.OfferSearchFilter;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/offers")
public class OfferController {

    private OfferService offerService;
    private UserService userService;
    private ModelMapper modelMapper;

    @Autowired
    public OfferController(
            final OfferService offerService,
            final ModelMapper modelMapper,
            final UserService userService
    ) {
        this.offerService = offerService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffers() {
        List<Offer> offers =  offerService.getAllOffers();
        List<OfferDto> list = MapToDto(offers);
        return list;
    }

    @GetMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersOnFilter(@RequestBody OfferSearchFilter filter) {
        Offer searchCriteriaOffer = modelMapper.map(filter, Offer.class);
        List<Offer> offers =  offerService.getByFilter(searchCriteriaOffer);
        List<OfferDto> list = MapToDto(offers);
        return list;
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto getOffer(@PathVariable String id) {
        Offer offer = this.offerService.getById(id);
        return MapSingleToDto(offer);
    }

    @GetMapping(value = "/title/{title}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByBookTitle(@PathVariable String title) {
        List<Offer> offers = this.offerService.getAllByBookTitle(title);
        return MapToDto(offers);
    }

    @GetMapping(value = "/publisher/{publisher}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByBookPublisher(@PathVariable String publisher) {
        List<Offer> offers = this.offerService.getAllByBookPublisher(publisher);
        return MapToDto(offers);
    }

    @GetMapping(value = "/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByOfferOwner(@PathVariable String id) {
        User user = userService.getUserById(id);
        List<Offer> offers = this.offerService.getAllByOfferOwner(user);
        return MapToDto(offers);
    }

    @GetMapping(value = "/city/{city}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByCity(@PathVariable String city) {
        List<Offer> offers = this.offerService.getAllByCity(city);
        return MapToDto(offers);
    }

    @GetMapping(value = "/voivodeship/{voivodeship}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByVoivodeship(@PathVariable String voivodeship) {
        List<Offer> offers = this.offerService.getAllByVoivodeship(voivodeship);
        return MapToDto(offers);
    }

    @GetMapping(value = "/name/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByName(@PathVariable String name) {
        List<Offer> offers = this.offerService.getAllByOfferName(name);
        return MapToDto(offers);
    }

    @GetMapping(value = "/between",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersBetweenDates(@RequestBody DatePairDto dates) {
        List<Offer> offers = this.offerService.getAllCreatedBetweenDates(dates.getStartDate(), dates.getEndDate());
        return MapToDto(offers);
    }

    @GetMapping(value = "/notexpired/{dateString}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersNotExpired(@PathVariable String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Offer> offers = this.offerService.getAllNotExpired(date);
        return MapToDto(offers);
    }

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto addOffer(@RequestBody OfferDto offerDto) {

        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        offer = this.offerService.addOffer(offer);
        return MapSingleToDto(offer);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto updateOffer(@PathVariable String id, @RequestBody OfferDto offerDto) {
        if(!id.equals(offerDto.getId())) {
            throw new ModifyException("Unmatching ids");
        }
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        offer = this.offerService.modifyOffer(offer);
        return MapSingleToDto(offer);
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteOffer(@PathVariable String id) {
        offerService.deleteOffer(id);
    }

    private OfferDto MapSingleToDto(Offer offer) {
        OfferDto mappedOfferDto = modelMapper.map(offer, OfferDto.class);
        return mappedOfferDto;
    }

    private List<OfferDto> MapToDto(List<Offer> offerList) {
        List<OfferDto> offersDto = new ArrayList<>();
        for(Offer offer : offerList) {
            OfferDto mappedOfferDto = MapSingleToDto(offer);
            offersDto.add(mappedOfferDto);
        }
        return offersDto;
    }
}
