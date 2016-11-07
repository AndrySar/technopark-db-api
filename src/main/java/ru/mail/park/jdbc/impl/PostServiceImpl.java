package ru.mail.park.jdbc.impl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.jdbc.IForumService;
import ru.mail.park.jdbc.IPostService;
import ru.mail.park.jdbc.IThreadService;
import ru.mail.park.jdbc.IUserService;
import ru.mail.park.model.Post;
import ru.mail.park.responses.DBResponse;
import ru.mail.park.responses.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

/**
 * Created by Andry on 07.11.16.
 */

@Service
@Transactional
public class PostServiceImpl extends BaseServiceImpl implements IPostService {
    private final DataSource ds;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    public PostServiceImpl(DataSource ds) {
        super(ds);
        this.tableName = Post.TABLE_NAME;
        this.ds = ds;
    }


    @Override
    public DBResponse create(String jsonString) {
        //System.out.println("Создаём Post");
        //System.out.println(jsonString);
        final Post post;
        try (Connection connection = ds.getConnection()) {
            post = new Post(new JsonParser().parse(jsonString).getAsJsonObject());

            String parentPatch = "";
            if (post.getParent() != null) {
                String query = new StringBuilder("SELECT patch FROM ")
                        .append(tableName)
                        .append(" WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, post.getParent());
                    LOGGER.info(ps.toString());
                    try (ResultSet resultSet = ps.executeQuery()) {
                        resultSet.next();
                        parentPatch = resultSet.getString("patch");
                    }
                }
            }
            System.out.println("Получили parentPatch = " + parentPatch);

            StringBuilder buildedQuery = new StringBuilder("SELECT MAX(patch) AS Max_patch FROM ")
                    .append(tableName)
                    .append(" WHERE thread = ? AND patch LIKE '");
            if (!parentPatch.equals("")) {
                System.out.println("parentPatch != null");
                buildedQuery.append(parentPatch);
            }
            buildedQuery.append("____'");

            String maxPatch = null;
            try (PreparedStatement ps = connection.prepareStatement(buildedQuery.toString())) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    maxPatch = resultSet.getString("Max_patch");
                }
            }
            System.out.println("Получили maxPatch = " + maxPatch);

            String resultPatch;
            if (maxPatch != null) {
                System.out.println("maxPatch != null");
                resultPatch = incPatch(maxPatch);
            } else {
                System.out.println("maxPatch == null");
                resultPatch = parentPatch + "0001";
            }
            System.out.println("Получили resultPatch = " + resultPatch);

            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(date, forum, isApproved, isDeleted, isEdited, isHighlighted, isSpam, message, parent, thread, user, patch)")
                    .append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, post.getDate());
                ps.setString(2, post.getForum().toString());
                ps.setBoolean(3, post.getIsApproved());
                ps.setBoolean(4, post.getIsDeleted());
                ps.setBoolean(5, post.getIsEdited());
                ps.setBoolean(6, post.getIsHighlighted());
                ps.setBoolean(7, post.getIsSpam());
                ps.setString(8, post.getMessage());
                ps.setObject(9, post.getParent());
                ps.setLong(10, Long.parseLong(post.getThread().toString()));
                ps.setString(11, post.getUser().toString());
                ps.setString(12, resultPatch);
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    post.setId(resultSet.getLong(1));
                    post.setPatch(resultPatch);
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                return handeSQLException(e);
            }
            query = "UPDATE Thread SET posts = posts + 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new DBResponse(Status.INVALID_REQUEST);
        }
        System.out.println("Post создан\n\n");
        return new DBResponse(Status.OK, post);
    }

    @Override
    public DBResponse details(long postId, String[] related, IUserService userService, IForumService forumService, IThreadService threadService) {
        Post post;
        try (Connection connection = ds.getConnection()) {
            String query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    post = new Post(resultSet);
                }
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
            if (related != null) {
                if (Arrays.asList(related).contains("forum")) {
                    post.setForum(forumService.details(post.getForum().toString(), null, userService).getObject());
                }
                if (Arrays.asList(related).contains("thread")) {
                    long threadId = Long.parseLong(post.getThread().toString());
                    post.setThread(threadService.details(threadId, null, userService, forumService).getObject());
                }
                if (Arrays.asList(related).contains("user")) {
                    post.setUser(userService.details(post.getUser().toString()).getObject());
                }
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, post);
    }

    @Override
    public DBResponse remove(String jsonString) {
        try (Connection connection = ds.getConnection()) {
            Long postId = new JsonParser().parse(jsonString).getAsJsonObject().get("post").getAsLong();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET isDeleted = 1 WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                new DBResponse(Status.NOT_FOUND);
            }

            Post post;
            query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    post = new Post(resultSet);
                }
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }

            query = "UPDATE Thread SET posts = posts - 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse restore(String jsonString) {
        try (Connection connection = ds.getConnection()) {
            Long postId = new JsonParser().parse(jsonString).getAsJsonObject().get("post").getAsLong();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET isDeleted = 0 WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                new DBResponse(Status.NOT_FOUND);
            }

            Post post;
            query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    post = new Post(resultSet);
                }
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }

            query = "UPDATE Thread SET posts = posts + 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public DBResponse update(String jsonString, IUserService userService, IForumService forumService, IThreadService threadService) {
        long postId;
        try (Connection connection = ds.getConnection()) {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            postId = object.get("post").getAsLong();
            String message = object.get("message").getAsString();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET message = ? WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, message);
                ps.setLong(2, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return new DBResponse(Status.OK, details(postId, null, userService, forumService, threadService).getObject());
    }

    @Override
    public DBResponse vote(String jsonString, IUserService userService, IForumService forumService, IThreadService threadService) {
        long postId;
        try (Connection connection = ds.getConnection()) {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            int vote = object.get("vote").getAsInt();
            postId = object.get("post").getAsLong();
            final String column = vote == -1 ? "dislikes" : "likes";
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
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new DBResponse(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new DBResponse(Status.INVALID_REQUEST);
        }

        return details(postId, null, userService, forumService, threadService);
    }

    @Override
    public DBResponse listForum(String forum, String since, Long limit, String order,  IUserService userService, IForumService forumService, IThreadService threadService) {
        return forumService.listPosts(forum, since, limit, order, null, userService, threadService);
    }

    @Override
    public DBResponse listThread(Long threadId, String since, Long limit, String order, IThreadService threadService) {
        return threadService.listPosts(threadId, since, limit, null, order);
    }

    public static String incPatch(String patch) {

        if (patch == null) {
            return "0001";
        }

        int patchLength = patch.length();

        if (!patch.endsWith("9")) {
            String lastStringNumber = patch.substring(patchLength - 1, patchLength);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 1) + lastNumber;
            return patch;
        }

        if (!patch.endsWith("99")) {
            String lastStringNumber = patch.substring(patchLength - 2, patchLength - 1);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 2) + lastNumber + "0";
            return patch;
        }

        if (!patch.endsWith("999")) {
            String lastStringNumber = patch.substring(patchLength - 3, patchLength - 2);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 3) + lastNumber + "00";
            return patch;
        }

        String lastStringNumber = patch.substring(patchLength - 4, patchLength - 3);
        int lastNumber = Integer.parseInt(lastStringNumber);
        lastNumber++;
        patch = patch.substring(0, patchLength - 4) + lastNumber + "000";

        return patch;
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
