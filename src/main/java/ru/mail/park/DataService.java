package ru.mail.park;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.IDataService;
import ru.mail.park.jdbc.IForumService;
import ru.mail.park.jdbc.IPostService;
import ru.mail.park.jdbc.IThreadService;
import ru.mail.park.jdbc.IUserService;
import ru.mail.park.jdbc.impl.ForumServiceImpl;
import ru.mail.park.jdbc.impl.PostServiceImpl;
import ru.mail.park.jdbc.impl.ThreadServiceImpl;
import ru.mail.park.jdbc.impl.UserServiceImpl;
import ru.mail.park.responses.DBResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andry on 06.11.16.
 */
@Service
public class DataService implements IDataService {

    private final IUserService userService;
    private final IThreadService threadService;
    private final IForumService forumService;
    private final IPostService postService;

    @Autowired
    public DataService(UserServiceImpl userServiceImpl, ThreadServiceImpl threadServiceImpl, ForumServiceImpl forumServiceImpl, PostServiceImpl postServiceImpl){
        this.userService = userServiceImpl;
        this.threadService = threadServiceImpl;
        this.forumService = forumServiceImpl;
        this.postService = postServiceImpl;
    }

    @Override
    public void truncateAllTables(){
        userService.truncateTable();
        forumService.truncateTable();
        threadService.truncateTable();
        postService.truncateTable();
    }

    @Override
    public Map<String, Long> getAmounts() {
        Map<String, Long> response = new HashMap<>();
        response.put("user", userService.getAmount());
        response.put("forum", forumService.getAmount());
        response.put("thread", threadService.getAmount());
        response.put("post", postService.getAmount());

        return response;
    }

    // User
    @Override
    public DBResponse createUser(String json){
        return userService.create(json);
    }

    @Override
    public DBResponse detailsUser(String email){
        return userService.details(email);
    }

    @Override
    public DBResponse followUser(String body){
        return userService.follow(body);
    }

    @Override
    public DBResponse unfollowUser(String data){
        return userService.unfollow(data);
    }

    @Override
    public DBResponse updateProfileUser(String data){
        return userService.updateProfile(data);
    }

    @Override
    public DBResponse listFollowersUser(String email, Long limit, String order, Long sinceId) {
        return userService.listFollowers(email, limit, order, sinceId);
    }

    @Override
    public DBResponse listFollowingUser(String email, Long limit, String order, Long sinceId) {
        return userService.listFollowing(email, limit, order, sinceId);
    }

    @Override
    public DBResponse listPostUser(String email, Long limit, String order, String since) {
        return userService.listPosts(email, limit, order, since);
    }

    // Forum
    @Override
    public DBResponse createForum(String json){
        return forumService.create(json);
    }

    @Override
    public DBResponse detailsForum(String shortName, String[] related){
        return forumService.details(shortName, related, userService);
    }

    @Override
    public DBResponse listPostsForum(String forum, String since, Long limit, String order, String[] related){
        return forumService.listPosts(forum, since, limit, order, related, userService, threadService);
    }

    @Override
    public DBResponse listThreadsForum(String forum, String since, Long limit, String order, String[] related){
        return forumService.listThreads(forum, since, limit, order, related, userService);
    }

    @Override
    public DBResponse listUsersForum(String forum, Long sinceId, Long limit, String order){
        return forumService.listUsers(forum, sinceId, limit, order);
    }

    // Thread
    @Override
    public DBResponse createThread(String json){
        return threadService.create(json);
    }

    @Override
    public DBResponse detailsThread(int threadId, String[] related){
        return threadService.details(threadId, related, userService, forumService);
    }

    @Override
    public DBResponse closeThread(String json){
        return threadService.close(json);
    }

    @Override
    public DBResponse openThread(String json){
        return threadService.open(json);
    }

    @Override
    public DBResponse removeThread(String json){
        return threadService.remove(json);
    }

    @Override
    public DBResponse restoreThread(String json){
        return threadService.restore(json);
    }

    @Override
    public DBResponse updateThread(String json){
        return threadService.update(json, userService, forumService);
    }

    @Override
    public DBResponse voteThread(String json){
        return threadService.vote(json, userService, forumService);
    }

    @Override
    public DBResponse subscribeThread(String json){
        return threadService.subscribe(json);
    }

    @Override
    public DBResponse unsubscribeThread(String json){
        return threadService.unsubscribe(json);
    }

    @Override
    public DBResponse listForumThread(String forum, String since, Long limit, String order){
        return threadService.listForum(forum, since, limit, order, userService, forumService);
    }


    @Override
    public DBResponse listUserThread(String user, String since, Long limit, String order){
        return threadService.listUser(user, since, limit, order);
    }

    @Override
    public DBResponse listPostsThread(Long threadId, String since, Long limit, String sort, String order){
        return threadService.listPosts(threadId, since, limit, sort, order);
    }

    // Post
    @Override
    public DBResponse createPost(String json){
        return postService.create(json);
    }

    @Override
    public DBResponse detailsPost(Long postId, String[] related){
        return postService.details(postId, related, userService, forumService, threadService);
    }

    @Override
    public DBResponse removePost(String json){
        return postService.remove(json);
    }

    @Override
    public DBResponse restorePost(String json){
        return postService.restore(json);
    }

    @Override
    public DBResponse updatePost(String json){
        return postService.update(json, userService, forumService, threadService);
    }

    @Override
    public DBResponse votePost(String json){
        return postService.vote(json, userService, forumService, threadService);
    }

    @Override
    public DBResponse listForumPost(String forum, String since, Long limit, String order) {
        return postService.listForum(forum, since, limit, order, userService, forumService, threadService);
    }

    @Override
    public DBResponse listThreadPost(Long threadId, String since, Long limit, String order) {
        return postService.listThread(threadId, since, limit, order, threadService);
    }


}
