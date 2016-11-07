package ru.mail.park.jdbc;

import ru.mail.park.responses.DBResponse;

/**
 * Created by Andry on 07.11.16.
 */
public interface IForumService {

    DBResponse create(String jsonString);

    DBResponse details(String shortName, String[] related, IUserService userService);

    DBResponse listPosts(String forum, String since, Long limit, String order, String[] related, IUserService userService, IThreadService threadService);

    DBResponse listThreads(String forum, String since, Long limit, String order, String[] related, IUserService userService);

    DBResponse listUsers(String forum, Long sinceId, Long limit, String order);

    void truncateTable();

    long getAmount();

}
