package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.*;
import net.fp.backBook.exceptions.*;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.StorageService;
import net.fp.backBook.services.UserDetectionService;
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
    private UserDetectionService userDetectionService;
    private ModelMapper modelMapper;

    @Autowired
    public OfferController(
            final OfferService offerService,
            final ModelMapper modelMapper,
            final UserService userService,
            final UserDetectionService userDetectionService,
            final StorageService storageService) {
        this.offerService = offerService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userDetectionService = userDetectionService;
        this.storageService = storageService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffers() {
        List<Offer> offers =  offerService.getAll();
        return mapToDto(offers);
    }

    @GetMapping(
            value = "p",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<OfferDto> getOffersByPage(@RequestParam("limit") int limit, @RequestParam("page") int page) {
        Page<Offer> offersPage =  offerService.getAllOffersByPage(page, limit);
        return mapToPageDto(offersPage);
    }


    @GetMapping(
            value = "/short",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OfferShortDto> getOffersShort() {
        List<Offer> offers =  offerService.getAll();
        return mapToShortDto(offers);
    }

    @GetMapping(
            value = "/short/p",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<OfferShortDto> getOffersShortByPage(@RequestParam("limit") int limit, @RequestParam("page") int page) {
        Page<Offer> offersPage =  offerService.getAllOffersByPage(page, limit);
        return mapToPageShortDto(offersPage);
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
        return mapToDto(offers);
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
        return mapToShortDto(offers);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto getOffer(@PathVariable String id) {
        Offer offer = this.offerService.getById(id);
        return mapSingleToDto(offer, OfferDto.class);
    }

    @GetMapping(value = "/title/{title}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByBookTitle(@PathVariable String title) {
        List<Offer> offers = this.offerService.getAllByBookTitle(title);
        return mapToDto(offers);
    }

    @GetMapping(value = "/publisher/{publisher}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByBookPublisher(@PathVariable String publisher) {
        List<Offer> offers = this.offerService.getAllByBookPublisher(publisher);
        return mapToDto(offers);
    }

    @GetMapping(value = "/user/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByOfferOwner(@PathVariable String id) {
        User user = userService.getById(id);
        List<Offer> offers = this.offerService.getAllByOfferOwner(user);
        return mapToDto(offers);
    }

    @GetMapping(value = "/city/{city}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByCity(@PathVariable String city) {
        List<Offer> offers = this.offerService.getAllByCity(city);
        return mapToDto(offers);
    }

    @GetMapping(value = "/voivodeship/{voivodeship}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByVoivodeship(@PathVariable String voivodeship) {
        List<Offer> offers = this.offerService.getAllByVoivodeship(voivodeship);
        return mapToDto(offers);
    }

    @GetMapping(value = "/name/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersByName(@PathVariable String name) {
        List<Offer> offers = this.offerService.getAllByOfferName(name);
        return mapToDto(offers);
    }

    @PostMapping(value = "/between",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersBetweenDates(@RequestBody DatePairDto dates) {
        if(dates == null)
            throw new GetException("Date-pair can't be null!");
        List<Offer> offers = this.offerService.getAllCreatedBetweenDates(dates.getStartDate(), dates.getEndDate());
        return mapToDto(offers);
    }

    @GetMapping(value = "/notexpired/{dateString}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OfferDto> getOffersNotExpired(@PathVariable String dateString) {
        if(dateString == null)
            throw new GetException("Date string is null!");
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Offer> offers = this.offerService.getAllNotExpired(date);
        return mapToDto(offers);
    }

    @PostMapping(value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto addOffer(@ModelAttribute OfferInputDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        offer.setOfferOwner(this.userDetectionService.getAuthenticatedUser());
        if (offerDto.getFile() != null) {
            String fileId = this.storageService.store(offerDto.getFile());
            offer.setFileId(fileId);
        }

        offer = this.offerService.add(offer);
        return mapSingleToDto(offer, OfferDto.class);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OfferDto updateOffer(@PathVariable String id, @ModelAttribute OfferInputDto offerDto) {
        if(!id.equals(offerDto.getId())) {
            throw new ModifyException("Unmatching ids");
        }

        User authenticatedUser = this.userDetectionService.getAuthenticatedUser();
        if (!this.offerService.existsByIdAndOfferOwner(id, authenticatedUser)) {
            throw new OwnerException("User is not user owner");
        }

        try {
            Offer offer = this.modelMapper.map(offerDto, Offer.class);
            offer.setOfferOwner(authenticatedUser);

            String fileId = this.offerService.getById(id).getFileId();
            if (fileId != null) {
                this.storageService.delete(fileId);
            }

            if (offerDto.getFile() != null) {
                fileId = this.storageService.store(offerDto.getFile());
                offer.setFileId(fileId);
            }

            offer = this.offerService.modify(offer);
            return mapSingleToDto(offer, OfferDto.class);
        } catch (Exception e) {
            throw new ModifyException(e);
        }
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteOffer(@PathVariable String id) {
        User authenticatedUser = this.userDetectionService.getAuthenticatedUser();
        if (!this.offerService.existsByIdAndOfferOwner(id, authenticatedUser)) {
            throw new OwnerException("User is not user owner");
        }

        String fileId;
        try {
            fileId = this.offerService.getById(id).getFileId();
        } catch (Exception e) {
            throw new DeleteException(e);
        }

        this.offerService.delete(id);

        if (fileId != null) {
            this.storageService.delete(fileId);
        }
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
        try {
            Offer offer = this.offerService.getById(id);
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
            throw new FileProcessingException("Exception during modification of offer. "
                    + modExc.getMessage());
        }
    }
    private <T> T mapSingleToDto(Offer offer, Class<T> cl) {
        return modelMapper.map(offer, cl);
    }

    private List<OfferShortDto> mapToShortDto(List<Offer> offers) {
        List<OfferShortDto> offersShortDto = new ArrayList<>();
        for(Offer offer : offers) {
            OfferShortDto mappedOfferDto =
                    mapSingleToDto(offer, OfferShortDto.class);
            offersShortDto.add(mappedOfferDto);
        }
        return offersShortDto;
    }

    private Page<OfferShortDto> mapToPageShortDto(Page<Offer> offers) {
        return new PageImpl<>(this.mapToShortDto(offers.getContent()));
    }

    private List<OfferDto> mapToDto(List<Offer> offerList) {
        List<OfferDto> offersDto = new ArrayList<>();
        for(Offer offer : offerList) {
            OfferDto mappedOfferDto =
                    mapSingleToDto(offer, OfferDto.class);
            offersDto.add(mappedOfferDto);
        }
        return offersDto;
    }

    private Page<OfferDto> mapToPageDto(Page<Offer> offersPage) {
        return new PageImpl<>(this.mapToDto(offersPage.getContent()));
    }
}
