package net.fp.backBook.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fp.backBook.configuration.HttpResponsesConfig;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.*;
import net.fp.backBook.exceptions.*;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.services.OfferService;
import net.fp.backBook.services.StorageService;
import net.fp.backBook.services.UserService;
import org.apache.tomcat.jni.Local;
import org.assertj.core.condition.DoesNotHave;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
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
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class OfferControllerTests {

    @InjectMocks
    private OfferController offerController;

    @Mock
    private UserService userService;

    @Mock
    private OfferService offerService;

    @Mock
    private StorageService storageService;

    @Mock
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private DateTimeFormatter dtF;

    private MockMvc mockMvc;

    @Before
    public void setupTests() {
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
        OfferSearchFilter offerSearchFilter = new OfferSearchFilter();
        Offer offer = new Offer();
        when(offerService.getByFilter(any(Offer.class))).thenThrow(GetException.class);
        when(modelMapper.map(offerSearchFilter, Offer.class)).thenReturn(offer);
        String path = "/offers/filter/";
        mockMvc.perform(post(path)
                .content(
                    objectMapper.writeValueAsString(offerSearchFilter)
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
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
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
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                        dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByBookTitle(anyString());
    }

    @Test
    public void testGetByTitleBadRequestOnGetException() throws Exception {
        when(offerService.getAllByBookTitle(anyString())).thenThrow(GetException.class);
        String path = "/offers/title/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetByPublisherReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByBookPublisher(anyString())).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/publisher/xxx";
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                        dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByBookPublisher(anyString());
    }

    @Test
    public void testGetByBookPublisherBadRequestOnGetException() throws Exception {
        when(offerService.getAllByBookPublisher(anyString())).thenThrow(GetException.class);
        String path = "/offers/publisher/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetByCityReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByCity(anyString())).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/city/xxx";
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                        dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByCity(anyString());
    }

    @Test
    public void testGetByBookCityBadRequestOnGetException() throws Exception {
        when(offerService.getAllByCity(anyString())).thenThrow(GetException.class);
        String path = "/offers/city/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetByVoivodeshipReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByVoivodeship(anyString())).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/voivodeship/xxx";
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                       dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                       dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                       dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByVoivodeship(anyString());
    }

    @Test
    public void testGetByBookVoivodeshipBadRequestOnGetException() throws Exception {
        when(offerService.getAllByVoivodeship(anyString())).thenThrow(GetException.class);
        String path = "/offers/voivodeship/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetByNameReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByOfferName(anyString())).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/name/xxx";
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                        dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByOfferName(anyString());
    }

    @Test
    public void testGetByBookNameBadRequestOnGetException() throws Exception {
        when(offerService.getAllByOfferName(anyString())).thenThrow(GetException.class);
        String path = "/offers/name/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetByOfferOwnerReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
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
                .offerOwner(fakeUserViewDto)
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
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/3")
                .build();
        offers.add(offer);
        offers.add(offer2);
        when(offerService.getAllByOfferOwner(any(User.class))).thenReturn(offers);
        when(userService.getById(anyString())).thenReturn(fakeUser);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(offer2, OfferDto.class)).thenReturn(offer2Dto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/user/xxx";
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
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
                .andExpect(jsonPath("$[1].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[1].createdAt").value(
                        dtF.format(offer2Dto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[1].expires").value(
                        dtF.format(offer2Dto.getExpires()))
                )
                .andExpect(jsonPath("$[1].active").value("true"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[1].url").value("/3"));
        verify(offerService).getAllByOfferOwner(any(User.class));
    }

    @Test
    public void testGetByOfferOwnerBadRequestOnGetException() throws Exception {
        when(offerService.getAllByOfferOwner(any(User.class))).thenThrow(GetException.class);
        when(userService.getById(anyString())).thenReturn(new User());
        String path = "/offers/user/xxx";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOffersBetweenDatesReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
                .lastName("lastName")
                .name("name")
                .build();
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
        OfferDto offerDto = OfferDto.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/2")
                .build();
        when(offerService.getAllCreatedBetweenDates(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(offer));
        when(userService.getById(anyString())).thenReturn(fakeUser);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/between/";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper
                        .writeValueAsString(new DatePairDto(LocalDateTime.now(), LocalDateTime.now()))))
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
                .andExpect(jsonPath("$[0].active").value("true"))
                .andExpect(jsonPath("$[0].city").value("city"))
                .andExpect(jsonPath("$[0].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[0].url").value("/2"));
        verify(offerService).getAllCreatedBetweenDates(
                any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    public void testGetOffersBetweenDatesBadRequestOnGetException() throws Exception {
        when(offerService.getAllCreatedBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(GetException.class);
        String path = "/offers/between/";
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper
                        .writeValueAsString(new DatePairDto(LocalDateTime.now(), LocalDateTime.now()))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetOffersNotExpiredDatesReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
                .lastName("lastName")
                .name("name")
                .build();
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
        OfferDto offerDto = OfferDto.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/2")
                .build();
        when(offerService.getAllNotExpired(
                any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(offer));
        when(userService.getById(anyString())).thenReturn(fakeUser);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);
        when(modelMapper.map(fakeUser, UserViewDto.class)).thenReturn(fakeUserViewDto);
        String path = "/offers/notexpired/" + LocalDateTime.now();
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
                .andExpect(jsonPath("$[0].offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$[0].createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$[0].expires").value(
                        dtF.format(offerDto.getExpires()))
                )
                .andExpect(jsonPath("$[0].active").value("true"))
                .andExpect(jsonPath("$[0].city").value("city"))
                .andExpect(jsonPath("$[0].voivodeship").value("voiv"))
                .andExpect(jsonPath("$[0].url").value("/2"));
        verify(offerService).getAllNotExpired(any(LocalDateTime.class));
    }

    @Test
    public void testGetOffersNotExpiredBadRequestOnGetException() throws Exception {
        when(offerService.getAllNotExpired(any(LocalDateTime.class)))
                .thenThrow(GetException.class);
        String path = "/offers/notexpired/" + LocalDateTime.now();
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testAddOfferReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
                .lastName("lastName")
                .name("name")
                .build();
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
        OfferDto offerDto = OfferDto.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/2")
                .build();
        when(offerService.add(offer)).thenReturn(offer);

        when(modelMapper.map(any(Offer.class), eq(OfferDto.class))).thenReturn(offerDto);
        when(modelMapper.map(any(OfferDto.class), eq(Offer.class))).thenReturn(offer);
        String path = "/offers";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(offerDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.bookTitle").value("title"))
                .andExpect(jsonPath("$.bookReleaseYear").value("1111"))
                .andExpect(jsonPath("$.offerName").value("name"))
                .andExpect(jsonPath("$.offerOwner.id").value("1"))
                .andExpect(jsonPath("$.offerOwner.email").value("email"))
                .andExpect(jsonPath("$.offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$.createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$.expires").value(
                        dtF.format(offerDto.getExpires()))
                )
                .andExpect(jsonPath("$.active").value("true"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.voivodeship").value("voiv"))
                .andExpect(jsonPath("$.url").value("/2"));
        verify(offerService).add(offer);
    }

    @Test
    public void testAddOfferIsNotAcceptableAddException() throws Exception {
        when(offerService.add(any(Offer.class))).thenThrow(AddException.class);
        when(modelMapper.map(new OfferDto(), Offer.class))
                .thenReturn(new Offer());
        String path = "/offers";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new OfferDto())))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testModifyOfferReturns() throws Exception {
        User fakeUser = User.builder()
                .id("1")
                .email("email")
                .password("password")
                .login("login")
                .lastName("lastName")
                .name("name")
                .build();
        UserViewDto fakeUserViewDto = UserViewDto.builder()
                .id("1")
                .email("email")
                .lastName("lastName")
                .name("name")
                .build();
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
        OfferDto offerDto = OfferDto.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("1111")
                .bookPublisher("publisher")
                .offerName("name")
                .offerOwner(fakeUserViewDto)
                .createdAt(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .active(true)
                .city("city")
                .voivodeship("voiv")
                .url("/2")
                .build();
        when(offerService.modify(any(Offer.class))).thenReturn(offer);
        when(modelMapper.map(any(Offer.class), eq(OfferDto.class))).thenReturn(offerDto);
        when(modelMapper.map(any(OfferDto.class), eq(Offer.class))).thenReturn(offer);
        String path = "/offers/" + offer.getId();
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(offerDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.bookTitle").value("title"))
                .andExpect(jsonPath("$.bookReleaseYear").value("1111"))
                .andExpect(jsonPath("$.offerName").value("name"))
                .andExpect(jsonPath("$.offerOwner.id").value("1"))
                .andExpect(jsonPath("$.offerOwner.email").value("email"))
                .andExpect(jsonPath("$.offerOwner.lastName").value("lastName"))
                .andExpect(jsonPath("$.createdAt").value(
                        dtF.format(offerDto.getCreatedAt()))
                )
                .andExpect(jsonPath("$.expires").value(
                        dtF.format(offerDto.getExpires()))
                )
                .andExpect(jsonPath("$.active").value("true"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.voivodeship").value("voiv"))
                .andExpect(jsonPath("$.url").value("/2"));
        verify(offerService).modify(offer);
    }

    @Test
    public void testAddUserIsConflictModifyException() throws Exception {
        when(offerService.modify(any(Offer.class))).thenThrow(ModifyException.class);
        String path = "/offers/1";
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new OfferDto())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteOfferSuccess() throws Exception {
        doNothing().when(offerService).delete(anyString());
        String path = "/offers/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());
        verify(offerService).delete(anyString());
    }

    @Test
    public void testDeleteOfferIsBadRequestDeleteException() throws Exception {
        doThrow(DeleteException.class).when(offerService).delete(anyString());
        String path = "/offers/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    /*
    * Files handling tests below
    *
     */

    @Test
    public void testUploadFileSuccessOnSave() throws Exception {
        Offer offer = Offer.builder().fileId("1").build();
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "file.data",
                        "text/plain", "data type".getBytes());
        when(this.storageService.store(any(MultipartFile.class)))
                .thenReturn("1");
        doNothing().when(this.storageService).delete("1");
        when(this.offerService.getById("1")).thenReturn(offer);
        when(this.offerService.modify(any(Offer.class))).thenReturn(offer);
        mockMvc.perform(multipart("/offers/1/file")
                .file(multipartFile))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testUploadFileNotAcceptableOnStorageServiceException() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "file.data",
                        "text/plain", "data type".getBytes());
        Offer offer = Offer.builder().fileId("1").build();
        when(this.storageService.store(any(MultipartFile.class)))
                .thenThrow(RuntimeException.class);
        doNothing().when(this.storageService).delete("1");
        when(this.offerService.getById("1")).thenReturn(offer);
        when(this.offerService.modify(any(Offer.class))).thenReturn(offer);
        mockMvc.perform(multipart("/offers/1/file")
                .file(multipartFile))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testUploadFileNotAcceptableOnStorageServiceDeleteException() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "file.data",
                        "text/plain", "data type".getBytes());
        Offer offer = Offer.builder().fileId("1").build();
        when(this.storageService.store(any(MultipartFile.class)))
                .thenReturn("1");
        doThrow(RuntimeException.class).when(this.storageService).delete("1");
        when(this.offerService.getById("1")).thenReturn(offer);
        when(this.offerService.modify(any(Offer.class))).thenReturn(offer);
        mockMvc.perform(multipart("/offers/1/file")
                .file(multipartFile))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testUploadFileNotAcceptableOnOfferServiceGetException() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "file.data",
                        "text/plain", "data type".getBytes());
        when(this.offerService.getById("1"))
                .thenThrow(GetException.class);
        when(this.storageService.store(any(MultipartFile.class)))
                .thenReturn("1");
        doNothing().when(this.storageService).delete("1");
        mockMvc.perform(multipart("/offers/1/file")
                .file(multipartFile))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testUploadFileNotAcceptableOnOfferServiceModifyException() throws Exception {
        Offer offer = mock(Offer.class);
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "file.data",
                        "text/plain", "data type".getBytes());
        when(this.offerService.modify(any(Offer.class)))
                .thenThrow(ModifyException.class);
        when(this.offerService.getById(anyString())).thenReturn(offer);
        when(this.storageService.store(any(MultipartFile.class)))
                .thenReturn("1");
        doNothing().when(this.storageService).delete("1");
        mockMvc.perform(multipart("/offers/1/file")
                .file(multipartFile))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetFileReturnsResource() throws Exception {
        Resource resource = new ByteArrayResource(new byte[] {1, 2, 3});
        Offer offer = Offer.builder().fileId("1").build();
        when(offerService.getById("1")).thenReturn(offer);
        when(storageService.load("1")).thenReturn(resource);
        mockMvc.perform(get("/offers/1/file"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFileNotFoundOnStorageServiceLoadException() throws Exception {
        Offer offer = Offer.builder().fileId("1").build();
        when(offerService.getById("1")).thenReturn(offer);
        when(storageService.load("1")).thenThrow(FileNotFound.class);
        mockMvc.perform(get("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testGetFileBadRequestOnOfferServiceGetByIdException() throws Exception {
        when(offerService.getById("1")).thenThrow(GetException.class);
        mockMvc.perform(get("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteFileSuccess() throws Exception {
        Offer offer = Offer.builder().fileId("1").build();
        doNothing().when(this.storageService).delete("1");
        when(this.offerService.getById("1")).thenReturn(offer);
        when(this.offerService.modify(any(Offer.class))).thenReturn(offer);
        mockMvc.perform(delete("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFileNotAcceptableOnStorageServiceException() throws Exception {
        Offer offer = Offer.builder().fileId("1").build();
        doThrow(RuntimeException.class).when(this.storageService).delete("1");
        when(this.offerService.getById("1")).thenReturn(offer);
        mockMvc.perform(delete("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteFileNotAcceptableOnOfferServiceGetException() throws Exception {
        when(this.offerService.getById("1"))
                .thenThrow(GetException.class);
        when(this.storageService.store(any(MultipartFile.class)))
                .thenReturn("1");
        mockMvc.perform(delete("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteFileNotAcceptableOnOfferServiceModifyException() throws Exception {
        Offer offer = mock(Offer.class);
        when(this.offerService.modify(any(Offer.class)))
                .thenThrow(ModifyException.class);
        when(this.offerService.getById(anyString())).thenReturn(offer);
        mockMvc.perform(delete("/offers/1/file"))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
