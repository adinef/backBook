package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.CounterOfferDto;
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

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DateTimeFormatter dateTimeFormatter;

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
    public void addCounterOfferByIdSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login")
                .password("password")
                .email("email")
                .name("name")
                .lastName("lastName")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(user)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now()).build();

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
                .andExpect(jsonPath("$.createdAt").value(dateTimeFormatter.format(counterOfferDto.getCreatedAt())))
                .andExpect(jsonPath("$.expires").value(dateTimeFormatter.format(counterOfferDto.getExpires())))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name"))
                .andExpect(jsonPath("$.user.lastName").value("lastName"))
                .andReturn();
    }

    @Test
    public void getCounterOfferByIdSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login")
                .password("password")
                .email("email")
                .lastName("lastName")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(user)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now()).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.getById(counterOffer.getId())).thenReturn(counterOffer);
        when(this.modelMapperMock.map(counterOffer, CounterOfferDto.class)).thenReturn(counterOfferDto);

        MvcResult result = this.mockMvc.perform(
                get("/counterOffers/" + counterOffer.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(this.objectMapper.writeValueAsString(counterOfferDto), content);
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
                .andReturn();
    }
}
