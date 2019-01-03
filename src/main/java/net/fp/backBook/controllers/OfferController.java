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
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersOnFilter(@RequestBody OfferSearchFilter filter) {
        try {
            Offer searchCriteriaOffer = modelMapper.map(filter, Offer.class);
            log.info("Mapped object: city: " + searchCriteriaOffer.getCity() );
            List<Offer> offers =  offerService.getByFilter(searchCriteriaOffer);
            log.info("Offers retrieved by city: " + offers.size());
            Type listType = new TypeToken< List<OfferDto> >() {}.getType();
            List<OfferDto> list = modelMapper.map(offers, listType);
            return list;
        } catch(final Exception e) {
         //TODO
            return null;
        }
    }

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto addOffer(@RequestBody OfferDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        offer = this.offerService.addOffer(offer);
        return this.modelMapper.map(offer, OfferDto.class);
    }

}
