package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.RentalDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.*;
import net.fp.backBook.services.CounterOfferService;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.RentalService;
import net.fp.backBook.services.UserService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class RentalControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RentalService rentalService;

    @Mock
    private CounterOfferService counterOfferService;

    @Mock
    private OfferService offerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RentalController rentalController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(rentalController)
                .setControllerAdvice(restResponseExceptionHandler).build();
    }

    @Test
    public void addRentalSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.add(rental)).thenReturn(rental);
        when(this.modelMapperMock.map(any(RentalDto.class), eq(Rental.class))).thenReturn(rental);
        when(this.modelMapperMock.map(any(Rental.class), eq(RentalDto.class))).thenReturn(rentalDto);

        this.mockMvc.perform(
                post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(rentalDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$.counterOffer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$.counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$.counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.counterOffer.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$.counterOffer.offer.city").value("city"))
                .andExpect(jsonPath("$.counterOffer.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.email").value("email2"));
    }

    @Test
    public void addRentalFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.add(rental)).thenThrow(AddException.class);
        when(this.modelMapperMock.map(any(RentalDto.class), eq(Rental.class))).thenReturn(rental);
        when(this.modelMapperMock.map(any(Rental.class), eq(RentalDto.class))).thenReturn(rentalDto);

        this.mockMvc.perform(
                post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(rentalDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void modifyRentalSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.modify(rental)).thenReturn(rental);
        when(this.modelMapperMock.map(any(RentalDto.class), eq(Rental.class))).thenReturn(rental);
        when(this.modelMapperMock.map(any(Rental.class), eq(RentalDto.class))).thenReturn(rentalDto);

        this.mockMvc.perform(
                put("/rentals/" + rental.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(rentalDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$.counterOffer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$.counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$.counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.counterOffer.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$.counterOffer.offer.city").value("city"))
                .andExpect(jsonPath("$.counterOffer.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.email").value("email2"));
    }

    @Test
    public void modifyRentalWrongIdFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.modify(rental)).thenReturn(rental);

        this.mockMvc.perform(
                put("/rentals/" + rental.getId() + 1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(rentalDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void modifyRentalDatabaseExceptionFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.modify(rental)).thenThrow(AddException.class);
        when(this.modelMapperMock.map(any(RentalDto.class), eq(Rental.class))).thenReturn(rental);

        this.mockMvc.perform(
                put("/rentals/" + rental.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(rentalDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getRentalByIdSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.rentalService.getById(rental.getId())).thenReturn(rental);
        when(this.modelMapperMock.map(rental, RentalDto.class)).thenReturn(rentalDto);

        this.mockMvc.perform(
                get("/rentals/" + rental.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$.counterOffer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$.counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$.counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.counterOffer.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$.counterOffer.offer.city").value("city"))
                .andExpect(jsonPath("$.counterOffer.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.email").value("email2"));
    }

    @Test
    public void getByIdRentalFailure() throws Exception {

        when(this.rentalService.getById(anyString())).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/rentals/1"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllRentalsSuccess() throws Exception {

        User user1 = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser1 = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer1 = Offer.builder()
                .id("1")
                .bookTitle("title1")
                .bookReleaseYear("2018")
                .bookPublisher("publisher1")
                .offerName("offerName1")
                .offerOwner(offerUser1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city1")
                .voivodeship("voivodeship1")
                .fileId("1")
                .build();

        CounterOffer counterOffer1 = CounterOffer.builder()
                .id("1")
                .offer(offer1)
                .user(user1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental1 = Rental.builder()
                .id("1")
                .user(user1)
                .counterOffer(counterOffer1)
                .offer(offer1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        User user2 = User.builder()
                .id("3")
                .login("login3")
                .password("password3")
                .email("email3")
                .name("name3")
                .lastName("lastName3")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser2 = User.builder()
                .id("4")
                .login("login4")
                .password("password4")
                .email("email4")
                .name("name4")
                .lastName("lastName4")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer2 = Offer.builder()
                .id("2")
                .bookTitle("title2")
                .bookReleaseYear("2018")
                .bookPublisher("publisher2")
                .offerName("offerName2")
                .offerOwner(offerUser2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city2")
                .voivodeship("voivodeship2")
                .fileId("2")
                .build();

        CounterOffer counterOffer2 = CounterOffer.builder()
                .id("2")
                .offer(offer2)
                .user(user2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental2 = Rental.builder()
                .id("2")
                .user(user2)
                .counterOffer(counterOffer2)
                .offer(offer2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto1 = this.modelMapper.map(rental1, RentalDto.class);
        RentalDto rentalDto2 = this.modelMapper.map(rental2, RentalDto.class);

        when(this.rentalService.getAll()).thenReturn(Lists.list(rental1, rental2));
        when(this.modelMapperMock.map(rental1, RentalDto.class)).thenReturn(rentalDto1);
        when(this.modelMapperMock.map(rental2, RentalDto.class)).thenReturn(rentalDto2);

        this.mockMvc.perform(
                get("/rentals"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[0].user.id").value("1"))
                .andExpect(jsonPath("$[0].user.name").value("name1"))
                .andExpect(jsonPath("$[0].user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].user.email").value("email1"))
                .andExpect(jsonPath("$[0].offer.id").value("1"))
                .andExpect(jsonPath("$[0].offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.active").value("true"))
                .andExpect(jsonPath("$[0].offer.city").value("city1"))
                .andExpect(jsonPath("$[0].offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[0].counterOffer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$[0].counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[0].counterOffer.offer.city").value("city1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[1].user.id").value("3"))
                .andExpect(jsonPath("$[1].user.name").value("name3"))
                .andExpect(jsonPath("$[1].user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].user.email").value("email3"))
                .andExpect(jsonPath("$[1].offer.id").value("2"))
                .andExpect(jsonPath("$[1].offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.active").value("true"))
                .andExpect(jsonPath("$[1].offer.city").value("city2"))
                .andExpect(jsonPath("$[1].offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.email").value("email4"))
                .andExpect(jsonPath("$[1].counterOffer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.user.id").value("3"))
                .andExpect(jsonPath("$[1].counterOffer.user.name").value("name3"))
                .andExpect(jsonPath("$[1].counterOffer.user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].counterOffer.user.email").value("email3"))
                .andExpect(jsonPath("$[1].counterOffer.offer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[1].counterOffer.offer.city").value("city2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.email").value("email4"));
    }

    @Test
    public void testDeleteRentalSuccess() throws Exception {

        doNothing().when(this.rentalService).delete(anyString());

        String path = "/rentals/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());

        verify(this.rentalService).delete(anyString());
    }

    @Test
    public void deleteRentalFailure() throws Exception {

        doThrow(DeleteException.class).when(this.rentalService).delete(anyString());

        String path = "/rentals/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getRentalByOfferSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.offerService.getById(offer.getId())).thenReturn(offer);
        when(this.rentalService.getByOffer(offer)).thenReturn(rental);
        when(this.modelMapperMock.map(rental, RentalDto.class)).thenReturn(rentalDto);

        this.mockMvc.perform(
                get("/rentals/offer/" + offer.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$.counterOffer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$.counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$.counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.counterOffer.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$.counterOffer.offer.city").value("city"))
                .andExpect(jsonPath("$.counterOffer.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.email").value("email2"));
    }

    @Test
    public void getRentalByOfferOfferServiceFailure() throws Exception {

        doThrow(GetException.class).when(this.offerService).getById(anyString());

        mockMvc.perform(get("/rentals/offer/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getRentalByOfferRentalServiceFailure() throws Exception {

        Offer offer = mock(Offer.class);

        when(this.offerService.getById(anyString())).thenReturn(offer);
        when(this.rentalService.getByOffer(offer)).thenThrow(GetException.class);

        mockMvc.perform(get("/rentals/offer/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getAllRentalsByUserSuccess() throws Exception {

        User user1 = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser1 = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer1 = Offer.builder()
                .id("1")
                .bookTitle("title1")
                .bookReleaseYear("2018")
                .bookPublisher("publisher1")
                .offerName("offerName1")
                .offerOwner(offerUser1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city1")
                .voivodeship("voivodeship1")
                .fileId("1")
                .build();

        CounterOffer counterOffer1 = CounterOffer.builder()
                .id("1")
                .offer(offer1)
                .user(user1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental1 = Rental.builder()
                .id("1")
                .user(user1)
                .counterOffer(counterOffer1)
                .offer(offer1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        User user2 = User.builder()
                .id("3")
                .login("login3")
                .password("password3")
                .email("email3")
                .name("name3")
                .lastName("lastName3")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser2 = User.builder()
                .id("4")
                .login("login4")
                .password("password4")
                .email("email4")
                .name("name4")
                .lastName("lastName4")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer2 = Offer.builder()
                .id("2")
                .bookTitle("title2")
                .bookReleaseYear("2018")
                .bookPublisher("publisher2")
                .offerName("offerName2")
                .offerOwner(offerUser2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city2")
                .voivodeship("voivodeship2")
                .fileId("2")
                .build();

        CounterOffer counterOffer2 = CounterOffer.builder()
                .id("2")
                .offer(offer2)
                .user(user2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental2 = Rental.builder()
                .id("2")
                .user(user1)
                .counterOffer(counterOffer2)
                .offer(offer2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto1 = this.modelMapper.map(rental1, RentalDto.class);
        RentalDto rentalDto2 = this.modelMapper.map(rental2, RentalDto.class);

        when(this.userService.getById(user1.getId())).thenReturn(user1);
        when(this.rentalService.getAllByUser(user1)).thenReturn(Lists.list(rental1, rental2));
        when(this.modelMapperMock.map(rental1, RentalDto.class)).thenReturn(rentalDto1);
        when(this.modelMapperMock.map(rental2, RentalDto.class)).thenReturn(rentalDto2);

        this.mockMvc.perform(
                get("/rentals/user/" + user1.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[0].user.id").value("1"))
                .andExpect(jsonPath("$[0].user.name").value("name1"))
                .andExpect(jsonPath("$[0].user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].user.email").value("email1"))
                .andExpect(jsonPath("$[0].offer.id").value("1"))
                .andExpect(jsonPath("$[0].offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.active").value("true"))
                .andExpect(jsonPath("$[0].offer.city").value("city1"))
                .andExpect(jsonPath("$[0].offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[0].counterOffer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$[0].counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[0].counterOffer.offer.city").value("city1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[1].user.id").value("1"))
                .andExpect(jsonPath("$[1].user.name").value("name1"))
                .andExpect(jsonPath("$[1].user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[1].user.email").value("email1"))
                .andExpect(jsonPath("$[1].offer.id").value("2"))
                .andExpect(jsonPath("$[1].offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.active").value("true"))
                .andExpect(jsonPath("$[1].offer.city").value("city2"))
                .andExpect(jsonPath("$[1].offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.email").value("email4"))
                .andExpect(jsonPath("$[1].counterOffer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.user.id").value("3"))
                .andExpect(jsonPath("$[1].counterOffer.user.name").value("name3"))
                .andExpect(jsonPath("$[1].counterOffer.user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].counterOffer.user.email").value("email3"))
                .andExpect(jsonPath("$[1].counterOffer.offer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[1].counterOffer.offer.city").value("city2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.email").value("email4"));
    }

    @Test
    public void getAllRentalsByUserUserServiceFailure() throws Exception {

        doThrow(GetException.class).when(this.userService).getById(anyString());

        mockMvc.perform(get("/rentals/user/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getAllRentalsByUserRentalServiceFailure() throws Exception {

        User user = mock(User.class);

        when(this.userService.getById(anyString())).thenReturn(user);
        when(this.rentalService.getAllByUser(user)).thenThrow(GetException.class);

        mockMvc.perform(get("/rentals/user/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getRentalByCounterOfferSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental = Rental.builder()
                .id("1")
                .user(user)
                .counterOffer(counterOffer)
                .offer(offer)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto = this.modelMapper.map(rental, RentalDto.class);

        when(this.counterOfferService.getById(counterOffer.getId())).thenReturn(counterOffer);
        when(this.rentalService.getByCounterOffer(counterOffer)).thenReturn(rental);
        when(this.modelMapperMock.map(rental, RentalDto.class)).thenReturn(rentalDto);

        this.mockMvc.perform(
                get("/rentals/counterOffer/" + counterOffer.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$.counterOffer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$.counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$.counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$.counterOffer.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.counterOffer.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$.counterOffer.offer.city").value("city"))
                .andExpect(jsonPath("$.counterOffer.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.counterOffer.offer.offerOwner.email").value("email2"));
    }

    @Test
    public void getRentalByCounterOfferOfferServiceFailure() throws Exception {

        doThrow(GetException.class).when(this.counterOfferService).getById(anyString());

        mockMvc.perform(get("/rentals/counterOffer/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getRentalByCounterOfferRentalServiceFailure() throws Exception {

        CounterOffer counterOffer = mock(CounterOffer.class);

        when(this.counterOfferService.getById(anyString())).thenReturn(counterOffer);
        when(this.rentalService.getByCounterOffer(counterOffer)).thenThrow(GetException.class);

        mockMvc.perform(get("/rentals/counterOffer/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getAllRentalsByNotExpiredSuccess() throws Exception {

        User user1 = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser1 = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer1 = Offer.builder()
                .id("1")
                .bookTitle("title1")
                .bookReleaseYear("2018")
                .bookPublisher("publisher1")
                .offerName("offerName1")
                .offerOwner(offerUser1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city1")
                .voivodeship("voivodeship1")
                .fileId("1")
                .build();

        CounterOffer counterOffer1 = CounterOffer.builder()
                .id("1")
                .offer(offer1)
                .user(user1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental1 = Rental.builder()
                .id("1")
                .user(user1)
                .counterOffer(counterOffer1)
                .offer(offer1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        User user2 = User.builder()
                .id("3")
                .login("login3")
                .password("password3")
                .email("email3")
                .name("name3")
                .lastName("lastName3")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser2 = User.builder()
                .id("4")
                .login("login4")
                .password("password4")
                .email("email4")
                .name("name4")
                .lastName("lastName4")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer2 = Offer.builder()
                .id("2")
                .bookTitle("title2")
                .bookReleaseYear("2018")
                .bookPublisher("publisher2")
                .offerName("offerName2")
                .offerOwner(offerUser2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city2")
                .voivodeship("voivodeship2")
                .fileId("2")
                .build();

        CounterOffer counterOffer2 = CounterOffer.builder()
                .id("2")
                .offer(offer2)
                .user(user2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        Rental rental2 = Rental.builder()
                .id("2")
                .user(user2)
                .counterOffer(counterOffer2)
                .offer(offer2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .startDate(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("03-03-2018 12:00:00", dateTimeFormatter)).build();

        RentalDto rentalDto1 = this.modelMapper.map(rental1, RentalDto.class);
        RentalDto rentalDto2 = this.modelMapper.map(rental2, RentalDto.class);

        when(this.rentalService.getAllByNotExpired(any(LocalDateTime.class))).thenReturn(Lists.list(rental1, rental2));
        when(this.modelMapperMock.map(rental1, RentalDto.class)).thenReturn(rentalDto1);
        when(this.modelMapperMock.map(rental2, RentalDto.class)).thenReturn(rentalDto2);

        this.mockMvc.perform(
                get("/rentals/notExpired/01-01-2018_12:00:00"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[0].user.id").value("1"))
                .andExpect(jsonPath("$[0].user.name").value("name1"))
                .andExpect(jsonPath("$[0].user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].user.email").value("email1"))
                .andExpect(jsonPath("$[0].offer.id").value("1"))
                .andExpect(jsonPath("$[0].offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.active").value("true"))
                .andExpect(jsonPath("$[0].offer.city").value("city1"))
                .andExpect(jsonPath("$[0].offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[0].counterOffer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.user.name").value("name1"))
                .andExpect(jsonPath("$[0].counterOffer.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].counterOffer.user.email").value("email1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.id").value("1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[0].counterOffer.offer.city").value("city1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].counterOffer.offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].startDate").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].expires").value("03-03-2018 12:00:00"))
                .andExpect(jsonPath("$[1].user.id").value("3"))
                .andExpect(jsonPath("$[1].user.name").value("name3"))
                .andExpect(jsonPath("$[1].user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].user.email").value("email3"))
                .andExpect(jsonPath("$[1].offer.id").value("2"))
                .andExpect(jsonPath("$[1].offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.active").value("true"))
                .andExpect(jsonPath("$[1].offer.city").value("city2"))
                .andExpect(jsonPath("$[1].offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.email").value("email4"))
                .andExpect(jsonPath("$[1].counterOffer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.user.id").value("3"))
                .andExpect(jsonPath("$[1].counterOffer.user.name").value("name3"))
                .andExpect(jsonPath("$[1].counterOffer.user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].counterOffer.user.email").value("email3"))
                .andExpect(jsonPath("$[1].counterOffer.offer.id").value("2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].counterOffer.offer.active").value("true"))
                .andExpect(jsonPath("$[1].counterOffer.offer.city").value("city2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].counterOffer.offer.offerOwner.email").value("email4"));
    }

    @Test
    public void getAllRentalsByNotExpiredFailure() throws Exception {

        doThrow(GetException.class).when(this.rentalService).getAllByNotExpired(any(LocalDateTime.class));

        mockMvc.perform(get("/rentals/notExpired/01-01-2018_12:00:00"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
