package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.CounterOfferDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.CounterOfferService;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class CounterOfferControllerTest {

    private MockMvc mockMvc;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CounterOfferService counterOfferService;

    @InjectMocks
    private CounterOfferController counterOfferController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(counterOfferController)
                .setControllerAdvice(restResponseExceptionHandler).build();
    }

    @Test
    public void addCounterOfferSuccess() throws Exception {

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

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.add(counterOffer)).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOffer.class), eq(CounterOfferDto.class))).thenReturn(counterOfferDto);

        this.mockMvc.perform(
                post("/counterOffers")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("02-02-2018 12:00:00"))
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
                .andReturn();
    }

    @Test
    public void addCounterOfferFailure() throws Exception {

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

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);
        when(this.counterOfferService.add(counterOffer)).thenThrow(AddException.class);

        this.mockMvc.perform(
                post("/counterOffers")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }

    @Test
    public void getCounterOfferByIdSuccess() throws Exception {

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

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.getById(counterOffer.getId())).thenReturn(counterOffer);
        when(this.modelMapperMock.map(counterOffer, CounterOfferDto.class)).thenReturn(counterOfferDto);

        this.mockMvc.perform(
                get("/counterOffers/" + counterOffer.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("02-02-2018 12:00:00"))
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
                .andReturn();
    }

    @Test
    public void getCounterOfferByIdFailure() throws Exception {

        String id = "1";

        when(this.counterOfferService.getById(id)).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/counterOffers/" + id))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }
}
