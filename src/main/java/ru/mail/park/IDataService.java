package ru.mail.park;

import ru.mail.park.responses.DBResponse;

import java.util.Map;

/**
 * Created by Andry on 06.11.16.
 */
public interface IDataService {

    void truncateAllTables();

    Map<String, Long> getAmounts();

    // User
    DBResponse createUser(String json);

    DBResponse detailsUser(String email);

    DBResponse followUser(String body);

    DBResponse unfollowUser(String body);

    DBResponse updateProfileUser(String body);

    DBResponse listFollowersUser(String email, Long limit, String order, Long sinceId);

    DBResponse listFollowingUser(String email, Long limit, String order, Long sinceId);

    DBResponse listPostUser(String email, Long limit, String order, String since);

    // Forum
    DBResponse createForum(String json);

    DBResponse detailsForum(String shortName, String[] related);

    DBResponse listPostsForum(String forum, String since, Long limit, String order, String[] related);

    DBResponse listThreadsForum(String forum, String since, Long limit, String order, String[] related);

    DBResponse listUsersForum(String forum, Long sinceId, Long limit, String order);

    // Thread
    DBResponse createThread(String json);

    DBResponse detailsThread(int threadId, String[] related);

    DBResponse closeThread(String json);

    DBResponse openThread(String json);

    DBResponse removeThread(String json);

    DBResponse restoreThread(String json);

    DBResponse updateThread(String json);

    DBResponse voteThread(String json);

    DBResponse subscribeThread(String json);

    DBResponse unsubscribeThread(String json);

    DBResponse listForumThread(String forum, String since, Long limit, String order);

    DBResponse listUserThread(String user, String since, Long limit, String order);

    DBResponse listPostsThread(Long threadId, String since, Long limit, String sort, String order);

    // Post
    DBResponse createPost(String json);

    DBResponse detailsPost(Long postId, String[] related);

    DBResponse removePost(String json);

    DBResponse restorePost(String json);

    DBResponse updatePost(String json);

    DBResponse votePost(String json);

    DBResponse listForumPost(String forum, String since, Long limit, String order);

    DBResponse listThreadPost(Long threadId, String since, Long limit, String order);

}
