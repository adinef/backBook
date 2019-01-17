package net.fp.backBook;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsTestController {

    @GetMapping("test-cors")
    @ResponseStatus(HttpStatus.OK)
    public void testCors() {

    }
}
