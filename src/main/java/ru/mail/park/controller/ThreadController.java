package ru.mail.park.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DataService;
import ru.mail.park.responses.Response;

/**
 * Created by Andry on 07.11.16.
 */

@RestController
@RequestMapping(value = "/db/api/thread")
public class ThreadController extends BaseController {

    @Autowired
    public ThreadController(DataService dataService){
        super(dataService);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody String body){
        return new Response(dbService.createThread(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public Response details(@RequestParam(value = "thread", required = true) int threadId,
                                @RequestParam(value = "related", required = false) String[] related) {
        return new Response(dbService.detailsThread(threadId, related));
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public Response close(@RequestBody String body) {
        return new Response(dbService.closeThread(body));
    }

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    public Response open(@RequestBody String body) {
        return new Response(dbService.openThread(body));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Response remove(@RequestBody String body) {
        return new Response(dbService.removeThread(body));
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public Response restore(@RequestBody String body) {
        return new Response(dbService.restoreThread(body));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(@RequestBody String body) {
        return new Response(dbService.updateThread(body));
    }

    @RequestMapping(value = "/vote", method = RequestMethod.POST)
    public Response vote(@RequestBody String body) {
        return new Response(dbService.voteThread(body));
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public Response subscribe(@RequestBody String body) {
        return new Response(dbService.subscribeThread(body));
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public Response unsubscribe(@RequestBody String body) {
        return new Response(dbService.unsubscribeThread(body));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"forum"})
    public Response listForum(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listForumThread(forum, since, limit, order));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"user"})
    public Response listUser(@RequestParam(value = "user", required = true) String user,
                                 @RequestParam(value = "since", required = false) String since,
                                 @RequestParam(value = "limit", required = false) Long limit,
                                 @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listUserThread(user, since, limit, order));
    }


    @RequestMapping(value = "/listPosts", method = RequestMethod.GET)
    public Response listPosts(@RequestParam(value = "thread", required = true) Long threadId,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "sort", required = false) String sort,
                                  @RequestParam(value = "order", required = false) String order) {
        return new Response(dbService.listPostsThread(threadId, since, limit, sort, order));
    }

}
