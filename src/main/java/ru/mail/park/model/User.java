package ru.mail.park.model;

import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Andry on 06.11.16.
 */
public class User {
    public static final String TABLE_NAME = "User";
    public static final String ABOUT_COLUMN = "about";
    public static final String EMAIL_COLUMN = "email";
    public static final String ID_COLUMN = "id";
    public static final String ISANONYMOUS_COLUMN = "isAnonymous";
    public static final String NAME_COLUMN = "name";
    public static final String USERNAME_COLUMN = "username";
    public static final String FOLLOWERS_COLUMN = "followers";
    public static final String FOLLOWING_COLUMN = "following";
    public static final String SUBSCRIPTIONS_COLUMN = "subscriptions";
    public static final String COMMA = ",";

    private String about;
    private String email;
    private long id;
    private boolean isAnonymous;
    private String name;
    private String username;
    private String[] followers;
    private String[] following;
    private Integer[] subscriptions;

    public User(String about, String email, long id, boolean isAnonymous, String name, String username) {
        this.about = about;
        this.email = email;
        this.id = id;
        this.isAnonymous = isAnonymous;
        this.name = name;
        this.username = username;
        this.followers = new String[]{};
        this.following = new String[]{};
        this.subscriptions = new Integer[]{};
    }

    public User(JsonObject object) {
        try {
            about = object.get(ABOUT_COLUMN).getAsString();
        } catch (Exception e) {
            about = null;
        }
        email = object.get(EMAIL_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isAnonymous = object.has(ISANONYMOUS_COLUMN) && object.get(ISANONYMOUS_COLUMN).getAsBoolean();
        try {
            name = object.get(NAME_COLUMN).getAsString();
        } catch (Exception e) {
            name = null;
        }
        try {
            username = object.get(USERNAME_COLUMN).getAsString();
        } catch (Exception e) {
            username = null;
        }
    }

//    public User(ResultSet resultSet) throws Exception {
//
//        about = resultSet.getString(ABOUT_COLUMN);
//        email = resultSet.getString(EMAIL_COLUMN);
//        id = resultSet.getLong(ID_COLUMN);
//        isAnonymous = resultSet.getBoolean(ISANONYMOUS_COLUMN);
//        name = resultSet.getString(NAME_COLUMN);
//        username = resultSet.getString(USERNAME_COLUMN);
//
//        final String tempFollowers = resultSet.getString(FOLLOWERS_COLUMN);
//        if (tempFollowers != null) {
//            this.followers = tempFollowers.split(COMMA);
//        } else {
//            this.followers = new String[]{};
//        }
//
//        final String tempFollowing = resultSet.getString(FOLLOWING_COLUMN);
//        if (tempFollowing != null) {
//            this.following = tempFollowing.split(COMMA);
//        } else {
//            this.following = new String[]{};
//        }
//
//        final String tempSubscriptions = resultSet.getString(SUBSCRIPTIONS_COLUMN);
//        if (tempSubscriptions != null) {
//            final String[] userSubs = tempSubscriptions.split(COMMA);
//            this.subscriptions = new Integer[userSubs.length];
//            for (int i = 0; i < userSubs.length; i++) {
//                this.subscriptions[i] = Integer.parseInt(userSubs[i]);
//            }
//        } else {
//            this.subscriptions = new Integer[]{};
//        }
//    }

    public User(ResultSet resultSet, boolean additional) throws SQLException {
        about = resultSet.getString(ABOUT_COLUMN);
        email = resultSet.getString(EMAIL_COLUMN);
        id = resultSet.getLong(ID_COLUMN);
        isAnonymous = resultSet.getBoolean(ISANONYMOUS_COLUMN);
        name = resultSet.getString(NAME_COLUMN);
        username = resultSet.getString(USERNAME_COLUMN);

        if (additional) {
            setAdditionalInfo(resultSet);
        }
    }

    public void setAdditionalInfo(ResultSet resultSet) throws SQLException {
        final String tempFollowers = resultSet.getString(FOLLOWERS_COLUMN);
        if (tempFollowers != null) {
            this.followers = tempFollowers.split(COMMA);
        } else {
            this.followers = new String[]{};
        }

        final String tempFollowing = resultSet.getString(FOLLOWING_COLUMN);
        if (tempFollowing != null) {
            this.following = tempFollowing.split(COMMA);
        } else {
            this.following = new String[]{};
        }

        final String tempSubscriptions = resultSet.getString(SUBSCRIPTIONS_COLUMN);
        if (tempSubscriptions != null) {
            final String[] userSubs = tempSubscriptions.split(COMMA);
            this.subscriptions = new Integer[userSubs.length];
            for (int i = 0; i < userSubs.length; i++) {
                this.subscriptions[i] = Integer.parseInt(userSubs[i]);
            }
        } else {
            this.subscriptions = new Integer[]{};
        }
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getFollowers() {
        return followers;
    }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public String[] getFollowing() {
        return following;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }

    public Integer[] getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Integer[] subscriptions) {
        this.subscriptions = subscriptions;
    }
}
