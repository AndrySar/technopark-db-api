package ru.mail.park.jdbc.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class ThreadServiceImpl extends BaseServiceImpl implements IThreadService {

    private final DataSource ds;

    public ThreadServiceImpl(DataSource ds) {
        super(ds);
        this.tableName = Thread.TABLE_NAME;
        this.ds = ds;
    }

    @Override
    public DBResponse create(String jsonString) {
        final ru.mail.park.model.Thread thread;
        try (Connection connection = ds.getConnection()){
            thread = new Thread(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(date, forum, isClosed, isDeleted, message, slug, title, user) ")
                    .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, thread.getDate());
                ps.setString(2, thread.getForum().toString());
                ps.setBoolean(3, thread.getIsClosed());
                ps.setBoolean(4, thread.getIsDeleted());
                ps.setString(5, thread.getMessage());
                ps.setString(6, thread.getSlug());
                ps.setString(7, thread.getTitle());
                ps.setString(8, thread.getUser().toString());
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    thread.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, thread);
    }

    @Override
    public DBResponse details(long threadId, String[] related, IUserService userService, IForumService forumService) {
        Thread thread;
        try (Connection connection = ds.getConnection()) {
            String query = new StringBuffer("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, threadId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    thread = new Thread(resultSet);
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
            if (related != null) {
                if (Arrays.asList(related).contains("thread")) {
                    return new DBResponse(Status.INCORRECT_REQUEST);
                }
                if (Arrays.asList(related).contains("forum")) {
                    thread.setForum(forumService.details(thread.getForum().toString(), null, userService).getObject());
                }
                if (Arrays.asList(related).contains("user")) {
                    thread.setUser(userService.details(thread.getUser().toString()).getObject());
                }
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, thread);
    }

    @Override
    public DBResponse close(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isClosed = 1 WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse open(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append("  SET isClosed = 0 WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse remove(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isDeleted = 1, posts = 0 WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
                query = "UPDATE Post SET isDeleted = 1 WHERE thread = ?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse restore(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = "UPDATE Post SET isDeleted = 0 WHERE thread = ?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.executeUpdate();
                }

                Long countPosts = null;
                query = "SELECT COUNT(*) AS countPosts FROM Post WHERE thread = ?";
                try(PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, thread);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        resultSet.next();
                        countPosts = resultSet.getLong("countPosts");
                    }
                }

                query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isDeleted = 0, posts = ? WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, countPosts);
                    ps.setInt(2, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse update(String jsonString, IUserService userService, IForumService forumService) {
        long threadId;
        try (Connection connection = ds.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            threadId = object.get("thread").getAsInt();
            String message = object.get("message").getAsString();
            String slug = object.get("slug").getAsString();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET message = ?, slug = ? WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, message);
                ps.setString(2, slug);
                ps.setLong(3, threadId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(threadId, null, userService, forumService);
    }

    @Override
    public DBResponse vote(String jsonString, IUserService userService, IForumService forumService) {
        long threadId;
        try (Connection connection = ds.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            Integer vote = object.get("vote").getAsInt();
            threadId = object.get("thread").getAsInt();
            String column = vote == -1 ? "dislikes" : "likes";
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET ")
                    .append(column)
                    .append(" = ")
                    .append(column)
                    .append(" + 1, points = points + ")
                    .append(Integer.toString(vote))
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, threadId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(threadId, null, userService, forumService);
    }

    @Override
    public DBResponse subscribe(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            String user = object.get("user").getAsString();
            Integer thread = object.get("thread").getAsInt();
            try {
                String query = "INSERT INTO Subscriptions (user, thread) VALUES (?,?)";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, user);
                    ps.setInt(2, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse unsubscribe(String jsonString) {
        try (Connection connection = ds.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();

            String user = object.get("user").getAsString();
            Integer thread = object.get("thread").getAsInt();

            try {
                String query = "DELETE FROM Subscriptions WHERE user = ? AND thread = ?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, user);
                    ps.setInt(2, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }
        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse listForum(String forum, String since, Long limit, String order, IUserService userService, IForumService forumService) {
        return forumService.listThreads(forum, since, limit, order, null, userService);
    }

    @Override
    public DBResponse listUser(String user, String since, Long limit, String order) {
        ArrayList<Thread> threads = new ArrayList<>();
        try (Connection connection = ds.getConnection())  {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE user = ?");

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
                ps.setString(1, user);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Thread thread = new Thread(resultSet);
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
    public DBResponse listPosts(Long threadId, String since, Long limit, String sort, String order) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            if (sort == null || sort.equals("flat")) {
                StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread = ? ");

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

                try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                    ps.setLong(1, threadId);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        while (resultSet.next()) {
                            posts.add(new Post(resultSet));
                        }
                        return new DBResponse(Status.OK, posts);
                    }
                } catch (SQLException e) {
                    return handeSQLException(e);
                }
            }

            if (sort.equals("tree")) {
                StringBuilder query = new StringBuilder();
                if (order == null || order.equals("desc")) {
                    System.out.println("TREE SORT DESC START");
                    query.append("SELECT patch FROM Post WHERE thread = ? ");

                    if (since != null) {
                        query.append("AND date >= '");
                        query.append(since);
                        query.append("' ");
                    }

                    query.append("AND patch LIKE '____' ");
                    query.append("ORDER BY patch DESC ");

                    if (limit != null) {
                        query.append("LIMIT ");
                        query.append(limit);
                    }

                    ArrayList<String> patches = new ArrayList<>();
                    try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                patches.add(resultSet.getString("patch"));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }

                    System.out.println("PATCHES: ");
                    for (int i = 0; i < patches.size(); i++) {
                        System.out.println(patches.get(i) + ",");
                    }

                    if (limit != null) {
                        for (int i = 0; i < patches.size() && posts.size() < limit; i++) {
                            long currentLimit = limit - posts.size();
                            StringBuilder unionQuery = new StringBuilder();
                            unionQuery.append("SELECT * FROM Post WHERE thread = ? AND patch LIKE '");
                            unionQuery.append(patches.get(i));
                            unionQuery.append("%' ORDER BY patch ASC LIMIT ");
                            unionQuery.append(currentLimit);

                            System.out.println(unionQuery);

                            try (PreparedStatement ps = connection.prepareStatement(unionQuery.toString())) {
                                ps.setLong(1, threadId);
                                try (ResultSet resultSet = ps.executeQuery()) {
                                    while (resultSet.next()) {
                                        posts.add(new Post(resultSet));
                                    }
                                }
                            } catch (SQLException e) {
                                return handeSQLException(e);
                            }
                        }
                    } else {
                        // При limit == null будет перевод на parent_sort DESC
                    }

                } else {
                    query.append("SELECT * FROM Post WHERE thread = ? ");

                    if (since != null) {
                        query.append("AND date >= '");
                        query.append(since);
                        query.append("' ");
                    }

                    query.append("ORDER BY patch ");
                    query.append("ASC ");

                    if (limit != null) {
                        query.append("LIMIT ");
                        query.append(limit);
                    }

                    try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                posts.add(new Post(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }
                }

            }

            if (sort.equals("parent_tree")) {
                System.out.println("PARENT TREE SORT START");
                StringBuilder query = new StringBuilder("SELECT patch FROM Post WHERE thread = ? ");

                if (since != null) {
                    query.append("AND date >= '");
                    query.append(since);
                    query.append("' ");
                }

                query.append("AND patch LIKE '____' ");

                query.append("ORDER BY patch ");
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

                ArrayList<String> patches = new ArrayList<>();
                try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                    ps.setLong(1, threadId);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        while (resultSet.next()) {
                            patches.add(resultSet.getString("patch"));
                        }
                    }
                } catch (SQLException e) {
                    return handeSQLException(e);
                }
                System.out.println("PATCHES: ");
                for (int i = 0; i < patches.size(); i++) {
                    System.out.println(patches.get(i) + ",");
                }

                for (int i = 0; i < patches.size(); i++) {
                    StringBuilder unionQuery = new StringBuilder();
                    unionQuery.append("SELECT * FROM Post WHERE thread = ? AND patch LIKE '");
                    unionQuery.append(patches.get(i));
                    unionQuery.append("%' ORDER BY patch ASC");

                    System.out.println(unionQuery);

                    try (PreparedStatement ps = connection.prepareStatement(unionQuery.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                posts.add(new Post(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }
                }

                System.out.println("POST PATCHES: ");
                for (int i = 0; i < posts.size(); i++) {
                    System.out.println(posts.get(i).getPatch() + ",");
                }
                System.out.println("PARENT TREE SORT END");
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
