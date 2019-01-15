package net.fp.backBook.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/docs")
    public String redirectSwagger() {
        return "redirect:/swagger-ui.html";
    }
}