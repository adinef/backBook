package net.fp.backBook.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderService(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

}
