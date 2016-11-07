package ru.mail.park.jdbc;

import ru.mail.park.responses.DBResponse;

/**
 * Created by Andry on 07.11.16.
 */
public interface IPostService {

    DBResponse create(String jsonString);

    DBResponse details(long postId, String[] related, IUserService userService, IForumService forumService, IThreadService threadService);

    DBResponse remove(String jsonString);

    DBResponse restore(String jsonString);

    DBResponse update(String jsonString, IUserService userService, IForumService forumService, IThreadService threadService);

    DBResponse vote(String jsonString, IUserService userService, IForumService forumService, IThreadService threadService);

    DBResponse listForum(String forum, String since, Long limit, String order,  IUserService userService, IForumService forumService, IThreadService threadService);

    DBResponse listThread(Long threadId, String since, Long limit, String order, IThreadService threadService);

    void truncateTable();

    long getAmount();


}
