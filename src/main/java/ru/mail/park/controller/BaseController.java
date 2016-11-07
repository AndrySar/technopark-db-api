package ru.mail.park.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.IDataService;
import ru.mail.park.DataService;

/**
 * Created by Andry on 06.11.16.
 */

@RestController
public abstract class BaseController {

    protected final IDataService dbService;

    @Autowired
    public BaseController(DataService dataService){
        this.dbService = dataService;
    }

}
