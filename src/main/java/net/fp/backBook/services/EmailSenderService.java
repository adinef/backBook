package net.fp.backBook.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import javax.mail.internet.MimeMessage;

public interface EmailSenderService {
    @Async
    void sendSimpleMail(SimpleMailMessage mail);

    @Async
    void sendMimeMail(MimeMessage mail);
}
