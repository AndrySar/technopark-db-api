package ru.mail.park.jdbc.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.controller.UserController;
import ru.mail.park.jdbc.IUserService;
import ru.mail.park.model.Post;
import ru.mail.park.model.User;
import ru.mail.park.responses.DBResponse;
import ru.mail.park.responses.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Andry on 06.11.16.
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements IUserService {

    private final DataSource ds;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(DataSource ds) {
        super(ds);
        this.tableName = User.TABLE_NAME;
        this.ds = ds;
    }

    public DBResponse create(String json){

        final User user;
        try (Connection connection = ds.getConnection()){
            user = new User(new JsonParser().parse(json).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                            .append(tableName)
                            .append("(about, email, isAnonymous, name, username) VALUES (?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getAbout());
                ps.setString(2, user.getEmail());
                ps.setBoolean(3, user.getIsAnonymous());
                ps.setString(4, user.getName());
                ps.setString(5, user.getUsername());
                ps.executeUpdate();
                LOGGER.info(ps.toString());
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    user.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }
        return new DBResponse(Status.OK, user);
    }

    @Override
    public DBResponse details(String email) {
        User user;
        try (Connection connection = ds.getConnection()) {
            String query = new StringBuilder("SELECT U.*, ")
                    .append("group_concat(distinct JUF1.follower) AS followers, ")
                    .append("group_concat(distinct JUF2.user) AS following, ")
                    .append("group_concat(distinct JUS.thread) AS subscriptions\n")
                    .append("FROM User U\n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("WHERE U.email = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    user = new User(resultSet);
                } catch (Exception e) {
                    return new DBResponse(Status.NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            return new DBResponse(Status.INCORRECT_REQUEST);
        }

        return new DBResponse(Status.OK, user);
    }

    @Override
    public DBResponse follow(String jsonString) {
        final String follower;
        try (Connection connection = ds.getConnection())  {
            final JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            follower = object.get("follower").getAsString();
            String followee = object.get("followee").getAsString();
            try {
                String query = "INSERT INTO Followers (user, follower) VALUES (?,?)";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, followee);
                    ps.setString(2, follower);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(follower);
    }

    @Override
    public DBResponse unfollow(String data) {
        final String follower;
        try (Connection connection = ds.getConnection())  {
            final JsonObject object = new JsonParser().parse(data).getAsJsonObject();
            follower = object.get("follower").getAsString();
            String followee = object.get("followee").getAsString();
            try {
                String query = "DELETE FROM Followers WHERE user=? AND follower=?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, followee);
                    ps.setString(2, follower);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(follower);
    }

    @Override
    public DBResponse updateProfile(String jsonString) {
        String email;
        try (Connection connection = ds.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            String about = object.get("about").getAsString();
            email = object.get("user").getAsString();
            String name = object.get("name").getAsString();
            String query = "UPDATE " + tableName + " SET about=?, name=? WHERE email=?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, about);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.executeUpdate();
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e ) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(email);
    }

    @Override
    public DBResponse listFollowers(String email, Long limit, String order, Long sinceId) {
        ArrayList<User> followers = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT U.*, ")
                    .append("group_concat(distinct JUF1.follower) AS followers, ")
                    .append("group_concat(distinct JUF2.user) AS following, ")
                    .append("group_concat(distinct JUS.thread) AS subscriptions\n")
                    .append("FROM Followers UF\n")
                    .append("JOIN User U ON U.email = UF.follower\n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("WHERE UF.user = ? ");

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
                ps.setString(1, email);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        followers.add(new User(resultSet));
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, followers);
    }

    @Override
    public DBResponse listFollowing(String email, Long limit, String order, Long sinceId) {
        ArrayList<User> followers = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT U.*, ")
                    .append("group_concat(distinct JUF1.follower) AS followers, ")
                    .append("group_concat(distinct JUF2.user) AS following, ")
                    .append("group_concat(distinct JUS.thread) AS subscriptions\n")
                    .append("FROM Followers UF\n")
                    .append("JOIN User U ON U.email = UF.user\n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("where UF.follower = ? ");

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
                ps.setString(1, email);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        followers.add(new User(resultSet));
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, followers);
    }

    @Override
    public DBResponse listPosts(String email, Long limit, String order, String since) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE user = ? ");
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
                ps.setString(1, email);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        posts.add(new Post(resultSet));
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
    public void truncateTable(){
        super.truncateTable();
    }

    @Override
    public long getAmount(){
       return super.getAmount();
    }
}
