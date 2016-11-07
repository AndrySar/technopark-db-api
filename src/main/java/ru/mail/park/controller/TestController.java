package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Andry on 06.11.16.
 */

@RestController
public class TestController {

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity test(){
        return ResponseEntity.ok("Test ok!");
    }
}
