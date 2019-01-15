package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.CounterOfferDto;
import net.fp.backBook.dtos.RentalDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.*;
import net.fp.backBook.services.CounterOfferService;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.RentalService;
import net.fp.backBook.services.UserService;
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
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    public void getByIdRentalSuccess() throws Exception {

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
}
