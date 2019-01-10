package net.fp.backBook.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author Adrian Fijalkowski
 */

@Configuration
public class RestTemplateConfig {
    @Bean
    public TestRestTemplate restTemplate() {
        return new TestRestTemplate();
    }
}
