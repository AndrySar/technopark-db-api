package ru.mail.park.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DataService;
import ru.mail.park.responses.Response;

/**
 * Created by Andry on 07.11.16.
 */

@RestController
@RequestMapping(value = "/db/api/forum")
public class ForumController extends BaseController {

    @Autowired
    public ForumController(DataService dataService){
        super(dataService);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumController.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody String body){
        return new Response(dbService.createForum(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public Response details(@RequestParam(value = "forum", required = true) String shortName,
                                @RequestParam(value = "related", required = false) String[] related ) {
        return new Response(dbService.detailsForum(shortName, related));
    }

    @RequestMapping(value = "/listPosts", method = RequestMethod.GET)
    public Response listPosts(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order,
                                  @RequestParam(value = "related", required = false) String[] related){
        return new Response(dbService.listPostsForum(forum, since, limit, order, related));
    }

    @RequestMapping(value = "/listThreads", method = RequestMethod.GET)
    public Response listThreads(@RequestParam(value = "forum", required = true) String forum,
                                    @RequestParam(value = "since", required = false) String since,
                                    @RequestParam(value = "limit", required = false) Long limit,
                                    @RequestParam(value = "order", required = false) String order,
                                    @RequestParam(value = "related", required = false) String[] related){
        return new Response(dbService.listThreadsForum(forum, since, limit, order, related));
    }

    @RequestMapping(value = "/listUsers", method = RequestMethod.GET)
    public Response listUsers(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since_id", required = false) Long sinceId,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listUsersForum(forum, sinceId, limit, order));
    }
}
