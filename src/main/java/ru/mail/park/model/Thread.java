package ru.mail.park.model;

import com.google.gson.JsonObject;

import java.sql.ResultSet;

/**
 * Created by Andry on 06.11.16.
 */
public class Thread {
    public static final String TABLE_NAME = "Thread";
    public static final String DATE_COLUMN = "date";
    public static final String DISLIKES_COLUMN = "dislikes";
    public static final String FORUM_COLUMN = "forum";
    public static final String ID_COLUMN = "id";
    public static final String ISCLOSED_COLUMN = "isClosed";
    public static final String ISDELETED_COLUMN = "isDeleted";
    public static final String LIKES_COLUMN = "likes";
    public static final String MESSAGE_COLUMN = "message";
    public static final String POINTS_COLUMN = "points";
    public static final String POSTS_COLUMN = "posts";
    public static final String SLUG_COLUMN = "slug";
    public static final String TITLE_COLUMN = "title";
    public static final String USER_COLUMN = "user";

    private String date;
    private long dislikes;
    private Object forum;
    private long id;
    private boolean isClosed;
    private boolean isDeleted;
    private long likes;
    private String message;
    private long points;
    private long posts;
    private String slug;
    private String title;
    private Object user;

    public Thread(JsonObject object) {
        date = object.get(DATE_COLUMN).getAsString().substring(0, 19);
        dislikes = 0;
        forum = object.get(FORUM_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isClosed = object.get(ISCLOSED_COLUMN).getAsBoolean();
        isDeleted = object.has(ISDELETED_COLUMN) && object.get(ISDELETED_COLUMN).getAsBoolean();
        likes = 0;
        message = object.get(MESSAGE_COLUMN).getAsString();
        points = 0;
        posts = 0;
        slug = object.get(SLUG_COLUMN).getAsString();
        title = object.get(TITLE_COLUMN).getAsString();
        user = object.get(USER_COLUMN).getAsString();
    }

    public Thread(ResultSet resultSet) throws Exception {
        date = resultSet.getString(DATE_COLUMN).substring(0, 19);
        dislikes = resultSet.getLong(DISLIKES_COLUMN);
        forum = resultSet.getString(FORUM_COLUMN);
        id = resultSet.getLong(ID_COLUMN);
        isClosed = resultSet.getBoolean(ISCLOSED_COLUMN);
        isDeleted = resultSet.getBoolean(ISDELETED_COLUMN);
        likes = resultSet.getLong(LIKES_COLUMN);
        message = resultSet.getString(MESSAGE_COLUMN);
        points = resultSet.getLong(POINTS_COLUMN);
        posts = resultSet.getLong(POSTS_COLUMN);
        slug = resultSet.getString(SLUG_COLUMN);
        title = resultSet.getString(TITLE_COLUMN);
        user = resultSet.getString(USER_COLUMN);
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

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}
