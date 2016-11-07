package ru.mail.park.jdbc;

import ru.mail.park.responses.DBResponse;

/**
 * Created by Andry on 06.11.16.
 */
public interface IUserService {

    DBResponse create(String json);

    DBResponse details(String email);

    DBResponse follow(String jsonString);

    DBResponse unfollow(String data);

    DBResponse updateProfile(String jsonString);

    DBResponse listFollowers(String email, Long limit, String order, Long sinceId);

    DBResponse listFollowing(String email, Long limit, String order, Long sinceId);

    DBResponse listPosts(String email, Long limit, String order, String since);

    void truncateTable();

    long getAmount();
}
