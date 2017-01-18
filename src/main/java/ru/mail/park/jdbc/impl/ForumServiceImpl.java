package ru.mail.park.jdbc.impl;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.jdbc.IForumService;
import ru.mail.park.jdbc.IThreadService;
import ru.mail.park.jdbc.IUserService;
import ru.mail.park.model.*;
import ru.mail.park.model.Thread;
import ru.mail.park.responses.DBResponse;
import ru.mail.park.responses.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Andry on 07.11.16.
 */
@Service
@Transactional
public class ForumServiceImpl extends BaseServiceImpl implements IForumService {

    private final DataSource ds;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumServiceImpl.class);

    public ForumServiceImpl(DataSource ds) {
        super(ds);
        this.tableName = Forum.TABLE_NAME;
        this.ds = ds;
    }

    @Override
    public DBResponse create(String jsonString) {
        final Forum forum;
        try (Connection connection = ds.getConnection()){
            forum = new Forum(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(name, short_name, user) VALUES (?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, forum.getName());
                ps.setString(2, forum.getShort_name());
                ps.setString(3, forum.getUser().toString());
                ps.executeUpdate();
                // LOGGER.info(ps.toString());
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    forum.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, forum);
    }

    @Override
    public DBResponse details(String shortName, String[] related, IUserService userService) {
        Forum forum;
        try (Connection connection = ds.getConnection()) {
            String query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE short_name = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, shortName);

                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();

                    forum = new Forum(resultSet);
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
            if (related != null) {
                if (Arrays.asList(related).contains("user")) {
                    String email = forum.getUser().toString();
                    forum.setUser(userService.details(email).getObject());
                }
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, forum);
    }

    @Override
    public DBResponse listPosts(String forum, String since, Long limit, String order, String[] related, IUserService userService, IThreadService threadService) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE forum = ? ");

            if (since != null) {
                query.append("AND date >= '");
                query.append(since);
                query.append("' ");
            }

            query.append("ORDER BY date ");
            if (order != null) {
                if (order.equals("asc")) {
                    query.append("ASC ");
                } else {
                    query.append("DESC ");
                }
            } else {
                query.append("DESC ");
            }

            if (limit != null) {
                query.append("LIMIT ");
                query.append(limit);
            }

            try(PreparedStatement ps = connection.prepareStatement(query.toString())) {
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Post post = new Post(resultSet);
                        if (related != null) {
                            if (Arrays.asList(related).contains("forum")) {
                                String shortName = post.getForum().toString();
                                post.setForum(this.details(shortName, null, userService).getObject());
                            }
                            if (Arrays.asList(related).contains("thread")) {
                                long threadId = Long.parseLong(post.getThread().toString());
                                post.setThread(threadService.details(threadId, null, userService, this).getObject());
                            }
                            if (Arrays.asList(related).contains("user")) {
                                String email = post.getUser().toString();
                                post.setUser(userService.details(email).getObject());
                            }
                        }
                        posts.add(post);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, posts);
    }

    @Override
    public DBResponse listThreads(String forum, String since, Long limit, String order, String[] related, IUserService userService) {
        ArrayList<Thread> threads = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE forum = ? ");

            if (since != null) {
                query.append("AND date >= '");
                query.append(since);
                query.append("' ");
            }

            query.append("ORDER BY date ");
            if (order != null) {
                if (order.equals("asc")) {
                    query.append("ASC ");
                } else {
                    query.append("DESC ");
                }
            } else {
                query.append("DESC ");
            }

            if (limit != null) {
                query.append("LIMIT ");
                query.append(limit);
            }

            try(PreparedStatement ps = connection.prepareStatement(query.toString())) {
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        ru.mail.park.model.Thread thread = new Thread(resultSet);
                        if (related != null) {
                            if (Arrays.asList(related).contains("forum")) {
                                String shortName = thread.getForum().toString();
                                thread.setForum(this.details(shortName, null, userService).getObject());
                            }
                            if (Arrays.asList(related).contains("user")) {
                                String email = thread.getUser().toString();
                                thread.setUser(userService.details(email).getObject());
                            }
                        }
                        threads.add(thread);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, threads);
    }

    @Override
    public DBResponse listUsers(String forum, Long sinceId, Long limit, String order) {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT U.* FROM User U\n")
                    .append("JOIN UsersOfForum UF ON U.email = UF.email\n")
                    .append("WHERE UF.forum = ? ");


            if (sinceId != null) {
                query.append("AND U.id >= ");
                query.append(sinceId);
                query.append(" ");
            }
            query.append("GROUP BY U.email ");
            query.append("ORDER BY U.name ");
            if (order != null) {
                if (order.equals("asc")) {
                    query.append("ASC ");
                } else {
                    query.append("DESC ");
                }
            } else {
                query.append("DESC ");
            }

            if (limit != null) {
                query.append("LIMIT ");
                query.append(limit);
            }

            try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        User temp = new User(resultSet, false);
                        users.add(temp);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }

            query = new StringBuilder("SELECT ")
                    .append("group_concat(distinct JUF1.follower) AS followers, ")
                    .append("group_concat(distinct JUF2.user) AS following, ")
                    .append("group_concat(distinct JUS.thread) AS subscriptions\n")
                    .append("FROM User U\n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("WHERE U.email = ? ");

            try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                for (User user: users) {
                    ps.setString(1, user.getEmail());
                    try (ResultSet resultSet = ps.executeQuery()) {
                        while (resultSet.next()) {
                            user.setAdditionalInfo(resultSet);
                        }
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }

        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, users);
    }

    @Override
    public void truncateTable(){
        super.truncateTable();
    }

    @Override
    public long getAmount(){
        return super.getAmount();
    }
}
