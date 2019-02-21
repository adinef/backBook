package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.EmailVerificationTokenRepository;
import net.fp.backBook.repositories.RoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/*
* @author Adrian Fijalkowski
*/

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class EmailVerificationTokenServiceTests {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @InjectMocks
    private EmailVerificationTokenServiceImpl tokenService;

    @Test
    public void testGetById() {
        EmailVerificationToken token = mock(EmailVerificationToken.class);
        when(this.tokenRepository.findById(anyString())).thenReturn(Optional.of(token));
        Assert.assertEquals(token, this.tokenService.getById(anyString()));
        verify(this.tokenRepository).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrowsOnRuntimeException() {
        when(this.tokenRepository.findById(anyString())).thenThrow(RuntimeException.class);
        this.tokenService.getById(anyString());
    }

    @Test
    public void testGetAll() {
        List<EmailVerificationToken> tokens = Arrays.asList(
                mock(EmailVerificationToken.class),
                mock(EmailVerificationToken.class)
        );
        when(this.tokenRepository.findAll()).thenReturn(tokens);
        Assert.assertEquals(tokens, this.tokenService.getAll());
        verify(this.tokenRepository).findAll();
    }

    @Test
    public void testGetAllReturnEmpty() {
        List<EmailVerificationToken> tokens = Collections.emptyList();
        when(this.tokenRepository.findAll()).thenReturn(tokens);
        Assert.assertEquals(tokens, this.tokenService.getAll());
        verify(this.tokenRepository).findAll();
    }

    @Test(expected = GetException.class)
    public void testGetAllThrowsOnRuntimeException() {
        when(this.tokenRepository.findAll()).thenThrow(RuntimeException.class);
        this.tokenService.getAll();
    }

    @Test
    public void testAdd() {
        EmailVerificationToken token = mock(EmailVerificationToken.class);
        when(this.tokenRepository.insert(token)).thenReturn(token);
        Assert.assertEquals(token, this.tokenService.add(token));
        verify(this.tokenRepository).insert(token);
    }

    @Test(expected = AddException.class)
    public void testAddThrowsGetOnRuntimeException() {
        EmailVerificationToken token = mock(EmailVerificationToken.class);
        when(this.tokenRepository.insert(token)).thenThrow(RuntimeException.class);
        this.tokenService.add(token);
    }


    @Test
    public void testDeleteRole() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setId("1");
        doNothing().when(this.tokenRepository).deleteById(token.getId());
        this.tokenService.delete(token.getId());
        verify(this.tokenRepository).deleteById(token.getId());
    }

    @Test(expected = DeleteException.class)
    public void testDeleteThrowsOnRuntimeException() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setId("1");
        doThrow(RuntimeException.class).when(this.tokenRepository).deleteById(token.getId());
        this.tokenService.delete(token.getId());
    }

    @Test
    public void testModify() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setId("1");
        when(this.tokenRepository.save(token)).thenReturn(token);
        Assert.assertEquals(token, this.tokenService.modify(token));
        verify(this.tokenRepository).save(token);
    }

    @Test(expected = ModifyException.class)
    public void testModifyThrowsOnIdNull() {
        EmailVerificationToken token = new EmailVerificationToken();
        Assert.assertEquals(token, this.tokenService.modify(token));
    }

    @Test(expected = ModifyException.class)
    public void testModifyThrowsOnRuntimeException() {
        EmailVerificationToken token = EmailVerificationToken
                .builder()
                .token("abcdefg")
                .id("1")
                .build();
        when(tokenRepository.save(any(EmailVerificationToken.class))).thenThrow(RuntimeException.class);
        this.tokenService.modify(token);
        verify(this.tokenRepository).save(token);
    }

    @Test
    public void testGetByToken() {
        EmailVerificationToken token = mock(EmailVerificationToken.class);
        when(this.tokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        Assert.assertEquals(token, this.tokenService.getVerificationTokenByRequested(anyString()));
        verify(this.tokenRepository).findByToken(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByTokenThrowsOnRuntimeException() {
        when(this.tokenRepository.findByToken(anyString())).thenThrow(RuntimeException.class);
        this.tokenService.getVerificationTokenByRequested(anyString());
    }
}
