package ru.mail.park.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DataService;
import ru.mail.park.responses.Response;


/**
 * Created by Andry on 06.11.16.
 */
@RestController
@RequestMapping(value = "/db/api/user")
public class UserController extends BaseController {

    @Autowired
    public UserController(DataService dataService){
        super(dataService);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody String body){
        return new Response(dbService.createUser(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public Response details(@RequestParam(value = "user") String email) {
        return new Response(dbService.detailsUser(email));
    }

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public Response follow(@RequestBody String body) {
        return new Response(dbService.followUser(body));
    }

    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    public Response unfollow(@RequestBody String body) {
        return new Response(dbService.unfollowUser(body));
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    public Response updateProfile(@RequestBody String body) {
        return new Response(dbService.updateProfileUser(body));
    }

    @RequestMapping(value = "/listFollowers", method = RequestMethod.GET)
    public Response listFollowers(@RequestParam(value = "user", required = true) String email,
                                      @RequestParam(value = "limit", required = false) Long limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "since_id", required = false) Long sinceId) {
        return new Response(dbService.listFollowersUser(email, limit, order, sinceId));
    }

    @RequestMapping(value = "/listFollowing", method = RequestMethod.GET)
    public Response listFollowing(@RequestParam(value = "user", required = true) String email,
                                      @RequestParam(value = "limit", required = false) Long limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "since_id", required = false) Long sinceId) {
        return new Response(dbService.listFollowingUser(email, limit, order, sinceId));
    }

    @RequestMapping(value = "/listPosts", method = RequestMethod.GET)
    public Response listPosts(@RequestParam(value = "user", required = true) String email,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order,
                                  @RequestParam(value = "since", required = false) String since){
        return new Response(dbService.listPostUser(email, limit, order, since));
    }
}
