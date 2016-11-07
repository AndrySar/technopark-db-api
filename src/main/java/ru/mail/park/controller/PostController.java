package ru.mail.park.controller;

import com.fasterxml.jackson.databind.deser.Deserializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DataService;
import ru.mail.park.responses.Response;

/**
 * Created by Andry on 07.11.16.
 */

@RestController
@RequestMapping(value = "/db/api/post")
public class PostController extends BaseController {

    @Autowired
    public PostController(DataService dataService){
        super(dataService);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody String body) {
        return new Response(dbService.createPost(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public Response details(@RequestParam(value = "post", required = true) long postId,
                                @RequestParam(value = "related", required = false) String[] related){
        return new Response(dbService.detailsPost(postId, related));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Response remove(@RequestBody String body) {
        return new Response(dbService.removePost(body));
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public Response restore(@RequestBody String body) {
        return new Response(dbService.restorePost(body));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(@RequestBody String body){
        return new Response(dbService.updatePost(body));
    }

    @RequestMapping(value = "/vote", method = RequestMethod.POST)
    public Response vote(@RequestBody String body){
        return new Response(dbService.votePost(body));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"forum"})
    public Response listForum(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listForumPost(forum, since, limit, order));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"thread"})
    public Response listUser(@RequestParam(value = "thread", required = true) Long threadId,
                                 @RequestParam(value = "since", required = false) String since,
                                 @RequestParam(value = "limit", required = false) Long limit,
                                 @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listThreadPost(threadId, since, limit, order));
    }

}
