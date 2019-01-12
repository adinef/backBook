package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.OfferDto;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * @author Adrian Fijalkowski
 */

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class OfferControllerTests {

    @InjectMocks
    private OfferController offerController;

    @Mock
    private UserService userService;

    @Mock
    private OfferService offerService;

    @Mock
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    private MockMvc mockMvc;

    @Before
    public void setServerAddress() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(offerController)
                .setControllerAdvice(restResponseExceptionHandler)
                .build();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.objectMapper.setDateFormat(simpleDateFormat);
    }

    @Test
    public void testGetOffersReturns() throws Exception {
        List<Offer> offers = Arrays.asList(mock(Offer.class), mock(Offer.class));
        when(offerService.getAll()).thenReturn(offers);
        when(modelMapper.map(offers.get(0), OfferDto.class)).thenReturn(new OfferDto());
        when(modelMapper.map(offers.get(1), OfferDto.class)).thenReturn(new OfferDto());
        mockMvc.perform(get("/offers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").value(hasSize(2)));
        verify(offerService).getAll();
    }

    @Test
    public void testGetOffersReturnsEmptyList() throws Exception {
        List<Offer> offers = Arrays.asList();
        when(offerService.getAll()).thenReturn(offers);
        mockMvc.perform(get("/offers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").isEmpty());
        verify(offerService).getAll();
    }

    @Test
    public void testGetOffersBadRequestOnGetException() throws Exception {
        when(offerService.getAll()).thenThrow(GetException.class);
        mockMvc.perform(get("/offers"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(offerService).getAll();
    }

    @Test
    public void testGetByIdReturns() throws Exception {
        Offer offer = Offer.builder().offerName("test").build();
        when(offerService.getById(anyString())).thenReturn(offer);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(OfferDto.builder().offerName("test").build());
        String path = "/offers/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.offerName").value("test"));
        verify(offerService).getById(anyString());
    }

    @Test
    public void testGetOfferByIdBadRequestOnGetException() throws Exception {
        when(offerService.getById(anyString())).thenThrow(GetException.class);
        String path = "/offers/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(offerService).getById(anyString());
    }

    @Test
    public void testGetOffersOnFilterReturns() throws Exception {
        OfferDto offerDto = OfferDto.builder().offerName("x").build();
        Offer offer = Offer.builder().offerName("x").build();
        when(modelMapper.map(offerDto, Offer.class)).thenReturn(offer);
        String path = "/offers/filter/";
        mockMvc.perform(post(path)
                .content(
                        objectMapper.writeValueAsString(offerDto)
                )
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    @Test
    public void testGetOffersOnFilterBadRequestOnGetException() throws Exception {
        OfferDto offerDto = new OfferDto();
        Offer offer = new Offer();
        when(offerService.getByFilter(any(Offer.class))).thenThrow(GetException.class);
        when(modelMapper.map(offerDto, Offer.class)).thenReturn(offer);
        String path = "/offers/filter/";
        mockMvc.perform(post(path)
                .content(
                    objectMapper.writeValueAsString(offerDto)
                )
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(offerService).getByFilter(any(Offer.class));
    }
    @Test
    public void testGetByTitleReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserDto fakeUserDto = UserDto.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        List<Offer> offers = new ArrayList<>();
        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUser)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .fileId("2")
                .build();
        Offer offer2 = Offer.builder()
                .id("2")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUser)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .fileId("3")
                .build();
        OfferDto offerDto = OfferDto.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/2")
                .build();
        OfferDto offer2Dto = OfferDto.builder()
                .id("2")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByBookTitle(anyString())).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserDto.class)).thenReturn(fakeUserDto);
        String path = "/offers/title/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].bookTitle").value("title"))
                .andExpect(jsonPath("$[0].bookReleaseYear").value("1111"))
                .andExpect(jsonPath("$[0].offerName").value("name"))
                .andExpect(jsonPath("$[0].offerOwner.id").value("1"))
                .andExpect(jsonPath("$[0].offerOwner.email").value("email"))
                .andExpect(jsonPath("$[0].offerOwner.password").value("password"))
                .andExpect(jsonPath("$[0].offerOwner.login").value("login"))
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                /*.andExpect(jsonPath("$[0].createdAt").value(
                        objectMapper.writeValueAsString(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        objectMapper.writeValueAsString(offerDto.getExpires()))
                )*/
                .andExpect(jsonPath("$[0].active").value("true"))
                .andExpect(jsonPath("$[0].city").value("city"))
                .andExpect(jsonPath("$[0].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[0].url").value("/2"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].bookTitle").value("title"))
                .andExpect(jsonPath("$[1].bookReleaseYear").value("1111"))
                .andExpect(jsonPath("$[1].offerName").value("name"))
                .andExpect(jsonPath("$[1].offerOwner.id").value("1"))
                .andExpect(jsonPath("$[1].offerOwner.email").value("email"))
                .andExpect(jsonPath("$[1].offerOwner.password").value("password"))
                .andExpect(jsonPath("$[1].offerOwner.login").value("login"))
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                /*.andExpect(jsonPath("$[1].createdAt").value(
                        objectMapper.writeValueAsString(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        objectMapper.writeValueAsString(offer2Dto.getExpires()))
                )*/
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByBookTitle(anyString());
    }
}
