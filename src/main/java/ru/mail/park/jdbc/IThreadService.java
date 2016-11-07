package ru.mail.park.jdbc;

import ru.mail.park.jdbc.impl.ForumServiceImpl;
import ru.mail.park.jdbc.impl.UserServiceImpl;
import ru.mail.park.responses.DBResponse;

/**
 * Created by Andry on 07.11.16.
 */
public interface IThreadService {

    DBResponse create(String jsonString);

    DBResponse details(long threadId, String[] related, IUserService userService, IForumService forumService);

    DBResponse close(String jsonString);

    DBResponse open(String jsonString);

    DBResponse remove(String jsonString);

    DBResponse restore(String jsonString);

    DBResponse update(String jsonString, IUserService userService, IForumService forumService);

    DBResponse vote(String jsonString, IUserService userService, IForumService forumService);

    DBResponse subscribe(String jsonString);

    DBResponse unsubscribe(String jsonString);

    DBResponse listForum(String forum, String since, Long limit, String order, IUserService userService, IForumService forumService);

    DBResponse listUser(String user, String since, Long limit, String order);

    DBResponse listPosts(Long threadId, String since, Long limit, String sort, String order);

    void truncateTable();

    long getAmount();


}
