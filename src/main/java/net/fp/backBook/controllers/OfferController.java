package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.OfferDto;
import net.fp.backBook.dtos.OfferSearchFilter;
import net.fp.backBook.model.Offer;
import net.fp.backBook.services.OfferService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/offers")
public class OfferController {

    private OfferService offerService;

    private ModelMapper modelMapper;

    @Autowired
    public OfferController(
            final OfferService offerService,
            final ModelMapper modelMapper
    ) {
        this.offerService = offerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersOnFilter() {
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

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto addOffer(@RequestBody OfferDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        offer = this.offerService.addOffer(offer);
        return this.modelMapper.map(offer, OfferDto.class);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto getOffer(@PathVariable String id) {
        Offer offer = this.offerService.getById(id);
        return this.modelMapper.map(offer, OfferDto.class);
    }

    @PutMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto updateOffer(@RequestBody OfferDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        offer = this.offerService.modifyOffer(offer);
        return this.modelMapper.map(offer, OfferDto.class);
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteOffer(@PathVariable String id) {
        offerService.deleteOffer(id);
    }

    private List<OfferDto> MapToDto(List<Offer> offerList) {
        Type listType = new TypeToken< List<OfferDto> >() {}.getType();
        return modelMapper.map(offerList, listType);
    }
}
