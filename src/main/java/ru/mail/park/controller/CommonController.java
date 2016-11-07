package ru.mail.park.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.DataService;
import ru.mail.park.responses.Response;

/**
 * Created by Andry on 06.11.16.
 */

@RestController
@RequestMapping(value = "/db/api")
public class CommonController extends BaseController{

    @Autowired
    public CommonController(DataService dataService){
        super(dataService);
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public Response clear() {
        dbService.truncateAllTables();
        return new Response("OK");
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public Response status() {
        return new Response(dbService.getAmounts());
    }

}
