package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.OfferDto;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
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
}
