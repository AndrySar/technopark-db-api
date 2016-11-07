package ru.mail.park.model;

import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Andry on 06.11.16.
 */
public class Post {
    public static final String TABLE_NAME = "Post";
    public static final String DATE_COLUMN = "date";
    public static final String DISLIKES_COLUMN = "dislikes";
    public static final String FORUM_COLUMN = "forum";
    public static final String ID_COLUMN = "id";
    public static final String ISAPPROVED_COLUMN = "isApproved";
    public static final String ISDELETED_COLUMN = "isDeleted";
    public static final String ISEDITED_COLUMN = "isEdited";
    public static final String ISHIGHLIGHTED_COLUMN = "isHighlighted";
    public static final String ISSPAM_COLUMN = "isSpam";
    public static final String LIKES_COLUMN = "likes";
    public static final String MESSAGE_COLUMN = "message";
    public static final String PARENT_COLUMN = "parent";
    public static final String POINTS_COLUMN = "points";
    public static final String THREAD_COLUMN = "thread";
    public static final String USER_COLUMN = "user";
    public static final String PATCH_COLUMN = "patch";

    private String date;
    private long dislikes;
    private Object forum;
    private long id;
    private boolean isApproved;
    private boolean isDeleted;
    private boolean isEdited;
    private boolean isHighlighted;
    private boolean isSpam;
    private long likes;
    private String message;
    private Long parent;
    private long points;
    private Object thread;
    private Object user;
    private String patch;

    public Post(JsonObject object) {
        date = object.get(DATE_COLUMN).getAsString();
        dislikes = 0;
        forum = object.get(FORUM_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isApproved = object.has(ISAPPROVED_COLUMN) && object.get(ISAPPROVED_COLUMN).getAsBoolean();
        isDeleted = object.has(ISDELETED_COLUMN) && object.get(ISDELETED_COLUMN).getAsBoolean();
        isEdited = object.has(ISEDITED_COLUMN) && object.get(ISEDITED_COLUMN).getAsBoolean();
        isHighlighted = object.has(ISHIGHLIGHTED_COLUMN) && object.get(ISHIGHLIGHTED_COLUMN).getAsBoolean();
        isSpam = object.has(ISSPAM_COLUMN) && object.get(ISSPAM_COLUMN).getAsBoolean();
        likes = 0;
        message = object.get(MESSAGE_COLUMN).getAsString();
        try {
            parent = object.get(PARENT_COLUMN).getAsLong();
        } catch (Exception e) {
            parent = null;
        }
        points = 0;
        thread = object.get(THREAD_COLUMN).getAsLong();
        user = object.get(USER_COLUMN).getAsString();
        patch = null;
    }

    public Post(ResultSet resultSet) throws SQLException {
        date = resultSet.getString(DATE_COLUMN).substring(0, 19);
        dislikes = resultSet.getLong(DISLIKES_COLUMN);
        forum = resultSet.getString(FORUM_COLUMN);
        id = resultSet.getLong(ID_COLUMN);
        isApproved = resultSet.getBoolean(ISAPPROVED_COLUMN);
        isDeleted = resultSet.getBoolean(ISDELETED_COLUMN);
        isEdited = resultSet.getBoolean(ISEDITED_COLUMN);
        isHighlighted = resultSet.getBoolean(ISHIGHLIGHTED_COLUMN);
        isSpam = resultSet.getBoolean(ISSPAM_COLUMN);
        likes = resultSet.getLong(LIKES_COLUMN);
        message = resultSet.getString(MESSAGE_COLUMN);
        parent = resultSet.getLong(PARENT_COLUMN);
        points = resultSet.getLong(POINTS_COLUMN);
        thread = resultSet.getLong(THREAD_COLUMN);
        user = resultSet.getString(USER_COLUMN);
        patch = resultSet.getString(PATCH_COLUMN);
        if (parent == 0) {
            parent = null;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public Object getForum() {
        return forum;
    }

    public void setForum(Object forum) {
        this.forum = forum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public boolean getIsHighlighted() {
        return isHighlighted;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public boolean getIsSpam() {
        return isSpam;
    }

    public void setIsSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Object getThread() {
        return thread;
    }

    public void setThread(Object thread) {
        this.thread = thread;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getPatch() {
        return patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }
}
