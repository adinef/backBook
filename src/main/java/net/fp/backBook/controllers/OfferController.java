package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.DatePairDto;
import net.fp.backBook.dtos.OfferDto;
import net.fp.backBook.dtos.OfferSearchFilter;
import net.fp.backBook.dtos.OfferShortDto;
import net.fp.backBook.exceptions.*;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.StorageService;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/offers")
public class OfferController {

    private OfferService offerService;
    private UserService userService;
    private StorageService storageService;
    private ModelMapper modelMapper;

    @Autowired
    public OfferController(
            final OfferService offerService,
            final ModelMapper modelMapper,
            final UserService userService,
            final StorageService storageService
    ) {
        this.offerService = offerService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffers() {
        List<Offer> offers =  offerService.getAll();
        return MapToDto(offers);
    }

    @GetMapping(
            value = "p",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<OfferDto> getOffersByPage(@RequestParam("limit") int limit, @RequestParam("page") int page) {
        Page<Offer> offersPage =  offerService.getAllOffersByPage(page, limit);
        return MapToPageDto(offersPage);
    }


    @GetMapping(
            value = "/short",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferShortDto> getOffersShort() {
        List<Offer> offers =  offerService.getAll();
        return MapToShortDto(offers);
    }

    @GetMapping(
            value = "/short/p",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<OfferShortDto> getOffersShortByPage(@RequestParam("limit") int limit, @RequestParam("page") int page) {
        Page<Offer> offersPage =  offerService.getAllOffersByPage(page, limit);
        return MapToPageShortDto(offersPage);
    }

    @PostMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersOnFilter(@RequestBody OfferSearchFilter filter) {
        Offer searchCriteriaOffer = modelMapper.map(filter, Offer.class);
        List<Offer> offers =  offerService.getByFilter(searchCriteriaOffer);
        return MapToDto(offers);
    }

    @PostMapping(
            value = "/short/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferShortDto> getOffersShortOnFilter(@RequestBody OfferSearchFilter filter) {
        Offer searchCriteriaOffer = modelMapper.map(filter, Offer.class);
        List<Offer> offers =  offerService.getByFilter(searchCriteriaOffer);
        return MapToShortDto(offers);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto getOffer(@PathVariable String id) {
        Offer offer = this.offerService.getById(id);
        return MapSingleToDto(offer, OfferDto.class);
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
        User user = userService.getById(id);
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

    @PostMapping(value = "/between",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersBetweenDates(@RequestBody DatePairDto dates) {
        if(dates == null)
            throw new GetException("Date-pair can't be null!");
        List<Offer> offers = this.offerService.getAllCreatedBetweenDates(dates.getStartDate(), dates.getEndDate());
        return MapToDto(offers);
    }

    @GetMapping(value = "/notexpired/{dateString}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersNotExpired(@PathVariable String dateString) {
        if(dateString == null)
            throw new GetException("Date string is null!");
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Offer> offers = this.offerService.getAllNotExpired(date);
        return MapToDto(offers);
    }

    @PostMapping(value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto addOffer(@ModelAttribute OfferDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        String fileId = null;
        if (offerDto.getFile() != null) {
            fileId = this.storageService.store(offerDto.getFile());
            offer.setFileId(fileId);
        }

        try {
            offer = this.offerService.add(offer);
        } catch (Exception e) {
            if (fileId != null) {
                this.storageService.delete(fileId);
            }
            throw e;
        }

        return MapSingleToDto(offer, OfferDto.class);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto updateOffer(@PathVariable String id, @RequestBody OfferDto offerDto) {
        if(offerDto == null)
            throw new ModifyException("Offer dto can't be null");
        if(!id.equals(offerDto.getId())) {
            throw new ModifyException("Unmatching ids");
        }
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        // set user from context here
        offer = this.offerService.modify(offer);
        return (OfferDto)MapSingleToDto(offer, OfferDto.class);
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteOffer(@PathVariable String id) {
        offerService.delete(id);
    }

    /*
    * Files handling below.
    *
     */
    @PostMapping(
            value = "/{id}/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
        String fileId = null;
        try {
            Offer offer = this.offerService.getById(id);
            this.storageService.delete(offer.getFileId());
            fileId = this.storageService.store(file);
            offer.setFileId(fileId);
            this.offerService.modify(offer);
        } catch( final GetException getExc) {
            throw new FileProcessingException("Exception during offer retrieval. "
                    + getExc.getMessage());
        } catch (final DeleteException delExc) {
            throw new FileProcessingException("Exception during file deletion. "
                    + delExc.getMessage());
        } catch( final AddException addExc) {
            throw new FileProcessingException("Exception during file persistence. "
                    + addExc.getMessage());
        } catch (final ModifyException modExc) {
            this.storageService.delete(fileId);
            throw new FileProcessingException("Exception during modification of offer. "
                    + modExc.getMessage());
        }
    }

    @GetMapping(
            value = "/{id}/file",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Resource downloadFile(@PathVariable("id") String id) {
        Offer offer = this.offerService.getById(id);
        return this.storageService.load(offer.getFileId());
    }

    @DeleteMapping(
            value = "/{id}/file"
    )
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable("id") String id) {

        String fileId = null;
        try {
            Offer offer = offer = this.offerService.getById(id);
            this.storageService.delete(offer.getFileId());
            offer.setFileId(null);
            this.offerService.modify(offer);
        } catch( final GetException getExc) {
            throw new FileProcessingException("Exception during offer retrieval. "
                    + getExc.getMessage());
        } catch (final DeleteException delExc) {
            throw new FileProcessingException("Exception during file deletion. "
                    + delExc.getMessage());
        } catch (final ModifyException modExc) {
            this.storageService.delete(fileId);
            throw new FileProcessingException("Exception during modification of offer. "
                    + modExc.getMessage());
        }
    }
    private <T> T MapSingleToDto(Offer offer, Class<T> cl) {
        return modelMapper.map(offer, cl);
    }

    private List<OfferShortDto> MapToShortDto(List<Offer> offers) {
        List<OfferShortDto> offersShortDto = new ArrayList<>();
        for(Offer offer : offers) {
            OfferShortDto mappedOfferDto =
                    MapSingleToDto(offer, OfferShortDto.class);
            offersShortDto.add(mappedOfferDto);
        }
        return offersShortDto;
    }

    private Page<OfferShortDto> MapToPageShortDto(Page<Offer> offers) {
        List<OfferShortDto> offersShortDto = new ArrayList<>();
        for(Offer offer : offers) {
            OfferShortDto mappedOfferDto =
                    MapSingleToDto(offer, OfferShortDto.class);
            offersShortDto.add(mappedOfferDto);
        }
        Page<OfferShortDto> page = new PageImpl<>(offersShortDto);
        return page;
    }

    private List<OfferDto> MapToDto(List<Offer> offerList) {
        List<OfferDto> offersDto = new ArrayList<>();
        for(Offer offer : offerList) {
            OfferDto mappedOfferDto =
                    MapSingleToDto(offer, OfferDto.class);
            offersDto.add(mappedOfferDto);
        }
        return offersDto;
    }

    private Page<OfferDto> MapToPageDto(Page<Offer> offersPage) {
        List<OfferDto> offersDto = new ArrayList<>();
        for(Offer offer : offersPage) {
            OfferDto mappedOfferDto =
                    MapSingleToDto(offer, OfferDto.class);
            offersDto.add(mappedOfferDto);
        }
        Page<OfferDto> page = new PageImpl<>(offersDto);
        return page;
    }
}
